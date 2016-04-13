package fr.univlille2.ecm.cover;

import java.io.IOException;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

public class CoverParser {

	private Exception error;
	private String decodedString;
	private CodingStrategy decoder;

	public CoverParser(CodingStrategy decoder) {
		this.error = null;
		this.decodedString = null;
		this.decoder = decoder;
	}

	public String decode(byte[] content) throws Exception {
		PdfReader reader = new PdfReader(content);
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);

		parser.processContent(1, new RenderListener() {

			@Override
			public void renderImage(ImageRenderInfo renderInfo) {

				PdfImageObject image = null;
				try {
					image = renderInfo.getImage();
				} catch (IOException err) {
					CoverParser.this.error = err;
				}
				if (image != null) {
					try {
						CoverParser.this.decodedString = CoverParser.this.decoder.decode(image);
					} catch (Exception err) {
						CoverParser.this.error = err;
					}
				}

			}

			@Override
			public void renderText(TextRenderInfo renderInfo) {
			}

			@Override
			public void endTextBlock() {
			}

			@Override
			public void beginTextBlock() {
			}

		});
		if (null != error)
			throw error;
		return decodedString;
	}

}
