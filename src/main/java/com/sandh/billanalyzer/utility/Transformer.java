package com.sandh.billanalyzer.utility;

import java.io.IOException;

public interface Transformer {
	String transform(ImageFilter imageFilterIn) throws IOException;

}
