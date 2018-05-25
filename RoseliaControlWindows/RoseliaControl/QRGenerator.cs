using System.Drawing;
using ZXing;
using ZXing.QrCode;

namespace RoseliaControl
{
    public class QrGenerator
    {
        private readonly BarcodeWriter _writer;

        public QrGenerator(int height, int width)
        {
            var options = new QrCodeEncodingOptions
            {
                DisableECI = true,
                CharacterSet = "utf-8",
                Width = width,
                Height = height
            };
            _writer = new BarcodeWriter
            {
                Format = BarcodeFormat.QR_CODE,
                Options = options
            };
        }

        public Bitmap Encode(string text)
        {
            return _writer.Write(text);
        }
    }
}