using System;
using System.ComponentModel;
using System.Drawing;
using System.Text.RegularExpressions;
using System.Windows.Forms;

namespace RoseliaControl
{
    public partial class Form1 : Form
    {
        private readonly RoseliaWebSocket _ws;

        public Form1()
        {
            InitializeComponent();
            const string host = "https://rc.roselia.xyz";
//            const string host = "http://localhost:9000";
            //var uri = "ws://localhost:9000/bind";
            var uri = $"{host.Replace("http", "ws")}/bind";
            var keyBoard = new KeyBoard();
            var encoder = new QrGenerator(qrCodeBox.Height, qrCodeBox.Width);
            Console.WriteLine(uri);
            _ws = new RoseliaWebSocket(uri);
            _ws.OnReceive(Console.WriteLine);
            _ws.OnReceive(s =>
            {
                var matchRes = Regex.Match(s, @"bind\|success\|(\d+)");
                if (matchRes.Success)
                {
                    var code = matchRes.Groups[1].Value;
                    SetTextOf(codeLabel, code);
                    SetPictureOf(qrCodeBox, encoder.Encode($"{host}/#/{code}"));
                }
            });
            _ws.OnReceive(s =>
            {
                if (s != "link|success") return;
                SetTextOf(codeLabel, "Controller Linked");
                SetPictureOf(qrCodeBox, null);
                SetEnableOf(quitButton, true);
            });
            _ws.OnReceive(s =>
            {
                var matchRes = Regex.Match(s, @"shortcut\|(\S+)");
                if (!matchRes.Success) return;
                var keyChar = matchRes.Groups[1].Value;
                keyBoard.SendShortCuts(keyChar);
            });
            _ws.OnReceive(s =>
            {
                var matchRes = Regex.Match(s, @"shutdown\|(\S+)");
                if (!matchRes.Success) return;
                _ws.Close();
                SetTextOf(codeLabel, "Disconnected");
                SetEnableOf(quitButton, true);
            });
            _ws.OnClose(() =>
            {
                SetTextOf(codeLabel, "Disconnected");
                SetEnableOf(quitButton, true);
            });
            _ws.Connect();
        }

        private void DoThreadSafe(Action f)
        {
            Invoke(f);
        }

        private void SetTextOf(Control lb, string text)
        {
            DoThreadSafe(() => lb.Text = text);
        }

        private void SetEnableOf(Control btn, bool f)
        {
            DoThreadSafe(() => btn.Enabled = f);
        }

        private void SetPictureOf(PictureBox pb, Image bm)
        {
            DoThreadSafe(() => pb.Image = bm);
        }

        private void quitButton_Click(object sender, EventArgs e)
        {
            _ws.Close();
            quitButton.Enabled = false;
            _ws.Connect();
        }
    }
}