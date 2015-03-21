import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.stream.FileImageOutputStream

if (args.length < 2)
{
   println "USAGE: groovy rescaleImage.groovy <sourceDirectory> <scalingFactor>\n"
   println "\twhere <sourceDirectory> is directory from which image files come"
   println "\t      <scalingFactor> is scaling factor for reduced image (0 to 1)"
   System.exit(-1)
}

def directoryPath = args[0]
def directory = new File(directoryPath)
if (!directory.isDirectory())
{
   println "${directoryPath} is NOT an existing directory!"
   System.exit(-1)
}

def scaleFactor = args[1] as float
def iter = ImageIO.getImageWritersByFormatName("jpeg")
def writer = (ImageWriter)iter.next()
def imageWriteParameters = writer.getDefaultWriteParam()
imageWriteParameters.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
imageWriteParameters.setCompressionQuality(scaleFactor);

def backupDirName = "backup_" + System.currentTimeMillis()
def targetDirectory = new File(backupDirName)
targetDirectory.mkdir()
def targetDirectoryPath = targetDirectory.canonicalPath
directory.eachFile
{ file ->
   def fullFileName = file.canonicalPath
   def fileName = file.name
   def sourceImage = ImageIO.read(new File(fullFileName))
   def targetName = targetDirectoryPath + File.separator + fileName
   println "Copying ${fullFileName} to ${targetName} with scale factor of ${scaleFactor} ..."
   def targetFile = new File(targetName);
   def output = new FileImageOutputStream(targetFile);
   writer.setOutput(output);
   def image = new IIOImage(sourceImage, null, null);
   writer.write(null, image, imageWriteParameters);
}
writer.dispose();
