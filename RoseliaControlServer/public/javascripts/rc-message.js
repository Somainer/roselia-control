;(function (window, undefined) {
    "use strict";
    window.app = {
        baseUri: `${window.location.protocol.replace('http', 'ws')}//${window.location.host}`,
        states: {
            clientId: "",
            isConnected: false,
            loading: false,
            sendKeys: "",
            pressedKeys: new Set()
        },
        neededKeys: ['F5', "Escape"]
    };

    class RoseliaSocket {
        constructor(uri) {
            this.socket = new WebSocket(uri);
            this.messageHooks = [];
            this.socket.onmessage = this.incoming.bind(this);
            this.timer = null;
            this.socket.addEventListener("open", _ => this.startPing());
            this.socket.addEventListener("close", _ => this.stopPing());
        }

        onMessage(...messages) {
            return func => {
                this.messageHooks.push({messages, func});
                return this;
            }
        }

        onConnect(func) {
            this.socket.addEventListener("open", mess => func(mess.data));
            return this;
        }

        onClose(func) {
            this.socket.addEventListener("close", mess => func(mess.data));
            return this;
        }

        onError(func) {
            this.socket.addEventListener("error", func);
            return this;
        }

        incoming(message) {
            message = message.data;
            let res = message.split('|');
            this.messageHooks.some(o => {
                let messages = o.messages, func = o.func;
                if (res.length === messages.length) {
                    if (messages.every((v, idx) => v === "*" || v === res[idx])) {
                        func(...res.filter((v, idx) => messages[idx] === "*"));
                        return true;
                    }
                }
                return false;
            });
        }

        send(...messages) {
            this.socket.send(messages.join('|'));
            return this;
        }

        close() {
            this.stopPing();
            this.send("shutdown", "bye");
            this.socket.close();
        }

        startPing() {
            this.timer && this.stopPing();
            this.timer = setInterval(_ => this.send("ping"), 5000);
        }

        stopPing() {
            clearInterval(this.timer);
            this.timer = null;
        }
    }

    app.notify = (...args) => Materialize.toast(...args);
    app.createSocket = function (uri) {
        uri = uri || `${this.baseUri}/control/${this.states.clientId}`;
        let socket = new RoseliaSocket(uri);
        return socket.onMessage("bind", "success", "*")(console.log)
            .onMessage("bind", "failed")(_ => this.stopConnection())
            .onMessage("bye", "*")(reason => {
                this.notify(reason, 1000);
                this.stopConnection()
            })
            .onConnect(_ => this.states.isConnected = true)
            .onClose(_ => this.states.isConnected = false)
            .onClose(_ => this.resetCid())
            .onError(_ => this.stopConnection());
    };
    app.getArgs = function () {
        const args = window.location.search;
        return args.substring((args.length > 2) + 0).split('&').map(s => s.split('=')).reduce((acc, [k, v]) => (acc[k] = v, acc), {});
    };
    app.getCid = function(){
        return (/(\d+)/.exec(window.location.hash) || [""])[0] || this.getArgs().cid || "";
    };
    app.resetCid = function () {
        window.history.pushState(null, '', '.');
    };
    app.setCid = function(cid) {
        window.history.pushState(null, '', `#/${cid || this.states.clientId}`);
    };
    app.startConnection = function () {
        this.states.loading = true;
        this.socket = this.createSocket();
        this.setCid();
        this.states.clientId = "";
        this.socket.onConnect(_ => {
            this.states.loading = false;
        });
    };
    app.stopConnection = function () {
        this.socket && this.socket.close();
        this.socket = null;
        this.states.isConnected = false;
        this.resetCid();
        // this.socket = this.createSocket(this.baseUri);
    };
    app.sendKeys = function (keys) {
        this.socket.send("shortcut", keys);
    };
    app.bindKeys = function () {
        const el = document.getElementById("send-keys");
        el.addEventListener("keydown", ev => {
            this.states.pressedKeys.add((ev.key === " " ? "Space" : ev.key) || ev.keyCode);
            this.states.sendKeys = Array.from(this.states.pressedKeys.values()).join('+');
            ev.preventDefault();
        });
        el.addEventListener('keyup', ev => {
            this.states.sendKeys && this.sendKeys(this.states.sendKeys);
            this.states.pressedKeys.clear();
            this.states.sendKeys = "";
        });
    };

    app.mainVue = new Vue({
        el: "#main-content",
        data: {app: window.app}
    });
    app.mainVue.$nextTick(_ => app.bindKeys());
    window.addEventListener('load', ev => {
        (app.states.clientId = app.getCid()) && app.startConnection();
    });
})(window);