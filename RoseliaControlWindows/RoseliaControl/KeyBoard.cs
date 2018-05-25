using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;

namespace RoseliaControl
{
    public class KeyBoard
    {
        private readonly Dictionary<string, string> _specialKeys = new Dictionary<string, string>();

        public KeyBoard()
        {
            var
                quotedKeys =
                    new[]
                    {
                        "alt", "backspace", "cancel", "capslock", "cleat", "control", "delete", "down", "end", "enter",
                        "escape", "f1", "f10", "f11", "f2", "f3", "f4", "f5", "f6", "f7", "f8", "f9", "home", "left",
                        "pagedown", "pageup", "pause", "right", "shift", "space", "tab", "up"
                    }.ToList();
            quotedKeys.ForEach(quotedKey => _specialKeys.Add(quotedKey, $"{{{quotedKey.ToUpper()}}}"));
            _specialKeys["shift"] = "+";
            _specialKeys["control"] = "^";
            _specialKeys["alt"] = "%";
        }

        private string translateKey(string key)
        {
            key = key.ToLower();
            return _specialKeys.ContainsKey(key) ? _specialKeys[key] : key;
        }

        private static string translateKey(int code)
        {
            return ((char) code).ToString();
        }

        public void SendShortCuts(string codes)
        {
            var parts = codes.Split('+').ToList().ConvertAll(translateKey);
            var final = string.Join("", parts.ToArray());
            Console.WriteLine(final);
            SendKeys.SendWait(final);
        }

        public void SendShortCuts(IEnumerable<int> codes)
        {
            SendKeys.SendWait(string.Join("", codes.ToList().ConvertAll(translateKey)));
        }
    }
}