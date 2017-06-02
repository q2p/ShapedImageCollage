package q2p.shapedimagecollage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

final class FilesProcessor {
	static final String mainFolder = "D:/@MyFolder/p/";
	static final String collageFolder = mainFolder+"other/ShapedCollageBuilder/";
	static final String sourceFolder = collageFolder+"src/";
	static final String scaledFolder = sourceFolder+"scaled/";
	static final String pixelsFolder = sourceFolder+"pixels/";
	static final String namesFile = sourceFolder+"names.txt";
	static final String shapeFolder = sourceFolder+"shape_images/";
	static final String outputFolder = collageFolder+"out/";

	/* TODO: private static final String mainFolder;
	static {
		final String path = normalizeURL(new File("test").getAbsolutePath(), false);

		if(!path.contains("/p/"))
			abort("Main folder not found.");

		mainFolder = path.substring(0, path.indexOf("/p/")+3);
	}*/
	
	static final void initDirectory(final String path) throws IOException {
		final File dir = new File(path);
		if(!dir.exists()) {
			if(!dir.mkdirs())
				throw new IOException("Failed to create dirrectory \""+path+"\".");
		} else if(!dir.isDirectory())
			throw new IOException("File \""+path+"\" is not a dirrectory.");

		final File[] files = dir.listFiles();
		for(final File file : files)
			if(!file.delete())
				throw new IOException("Failed to delete file \""+path+"\".");
	}
	private static final void initFile(final String path) throws IOException {
		final File file = new File(path);
		if(!file.exists()) {
			if(!file.createNewFile())
				throw new IOException("Failed to create file \""+path+"\".");
		} else if(!file.isFile())
			throw new IOException("File \""+path+"\" is not a file.");
	}
	static final boolean cleanUpPart(final int id) {
		File file = new File(pixelsFolder+id+".png");
		if(file.exists() || !file.delete())
			return false;
		file = new File(scaledFolder+id+".png");
		if(file.exists() || !file.delete())
			return false;
		
		return true;
	}
	private static final boolean swapPartName(final int fromId, final int toId) {
		File file = new File(pixelsFolder+fromId+".png");
		if(!file.exists() || !file.renameTo(new File(pixelsFolder+toId+".png")))
			return false;
		file = new File(scaledFolder+fromId+".png");
		if(!file.exists() || !file.renameTo(new File(scaledFolder+toId+".png")))
			return false;
		
		return true;
	}

	static final boolean savePartsNamesToFile() throws IOException {
		try {
			initFile(namesFile);
		} catch(final IOException e) {
			UserInterface.nonFatalIO(e.getMessage(), e);
			return false;
		}
		
		FileWriter writer = null;

		int realId = 0;
		
		boolean needBreak = false;
		try {
			writer = new FileWriter(new File(namesFile));
			for(Iterator<PartUnit> iterator = PartsBuilder.parts.iterator(); iterator.hasNext();) {
				final PartUnit part = iterator.next();
				if(!part.done)
					continue;
				
				if(needBreak)
					writer.write("\n");
				needBreak = true;
				
				writer.write(part.path);
				writer.flush();
				
				if(!swapPartName(part.id, realId)) {
					System.out.println(("Failed to swap part file with id "+part.id+" to "+realId));
					writer.close();
					return false;
				}
				
				realId++;
			}
			return true;
		} catch(final IOException e) {
			UserInterface.nonFatalIO(e.getMessage(), e);
			return false;
		} finally {
			if(writer != null)
				writer.close();
		}
	}

	static final void loadFromDir(final String path) {
		final String[] files = new File(path).list();
		for(final String file : files)
			if(isImage(path+file))
				PartsBuilder.parts.addLast(new PartUnit(path+file, PartsBuilder.parts.size()));
	}
	
	static final void loadFromDirRecursion(final File dir) {
		final File[] files = dir.listFiles();
		for(final File file : files) {
			if(file.isDirectory()) {
				loadFromDirRecursion(file);
			} else if(isImage(file.getName())) {
				PartsBuilder.parts.addLast(new PartUnit(file.getAbsolutePath(), PartsBuilder.parts.size()));
			}
		}
	}

	private static final boolean isImage(final String path) {
		return
				endsWith(path, ".png") ||
				endsWith(path, ".jpg") ||
				endsWith(path, ".gif") ||
				endsWith(path, ".jpeg");
	}
	
	static final boolean endsWith(final String string, final String lowerCaseEnding) {
		return string.toLowerCase().endsWith(lowerCaseEnding);
	}
}