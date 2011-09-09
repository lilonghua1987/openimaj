/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *   * 	Redistributions of source code must retain the above copyright notice,
 * 	this list of conditions and the following disclaimer.
 *
 *   *	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 *
 *   *	Neither the name of the University of Southampton nor the names of its
 * 	contributors may be used to endorse or promote products derived from this
 * 	software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.openimaj.tools.imagecollection.processor;

import java.io.File;
import java.io.IOException;

import org.openimaj.image.Image;
import org.openimaj.image.ImageUtilities;
import org.openimaj.io.FileUtils;
import org.openimaj.tools.imagecollection.collection.ImageCollectionEntry;

public class DirectoryImageProcessor<T extends Image<?, T>> extends ImageCollectionProcessor<T> {

	File directoryFile = new File(".");
	boolean force = true;
	String imageOutputFormat = "%s.png";

	public DirectoryImageProcessor(String output, boolean force,String imageNameFormat) {
		this.directoryFile = new File(output);
		this.force = force;
		this.imageOutputFormat = imageNameFormat;
	}

	@Override
	public void start() throws IOException{
		if(this.directoryFile.exists()){
			if(this.directoryFile.isDirectory() ){
				if(force) FileUtils.deleteRecursive(this.directoryFile);
			}
		}
		if(!this.directoryFile.mkdirs()){
			throw new IOException("Can't create directory");
		}
		// Directory should exist and be a directory now
	}

	@Override
	public void process(ImageCollectionEntry<T> image) throws IOException{
		if(image.accepted){
			File imageOutput = new File(this.directoryFile,String.format(imageOutputFormat,image.name));
			ImageUtilities.write(image.image, imageOutput);
		}
	}

}