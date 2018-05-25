using System;
using System.Timers;
using WebSocketSharp;

namespace RoseliaControl
{
    public class RoseliaWebSocket
    {
        private readonly WebSocket _ws;
        public bool IsOpened => _ws.IsAlive;
        public RoseliaWebSocket(string uri)
        {
            _ws = new WebSocket(uri);
            var pingTimer = new Timer(5000)
            {
                AutoReset = true,
                Enabled = false
            };
            pingTimer.Elapsed += (sender, ev) => Send("ping");
            OnOpen(() => pingTimer.Enabled = true);
            OnClose(() => pingTimer.Enabled = false);
        }

        public void Connect()
        {
            if(!IsOpened) _ws.ConnectAsync();
        }

        public void Close()
        {
            if(IsOpened) _ws.CloseAsync();
        }

        public void OnReceive(Action<string> func)
        {
            _ws.OnMessage += (sender, ev) => func(ev.Data);
        }

        public void OnClose(Action func)
        {
            _ws.OnClose += (sender, ev) => func();
        }

        public void OnOpen(Action func)
        {
            _ws.OnOpen += (sender, ev) => func();
        }

        public void Send(string mess, Action<bool> onComplete = null)
        {
            _ws.SendAsync(mess, onComplete);
        }
    }
}