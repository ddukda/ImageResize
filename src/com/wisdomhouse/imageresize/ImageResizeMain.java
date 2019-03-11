package com.wisdomhouse.imageresize;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageResizeMain {
	static final int _HEIGHT = 1080;
	static final int _WIDTH = 720;
	
	public static void main(String[] args) {
		ImageResizeProc irp = new ImageResizeProc();
		
		// 프로그램이 실행되는 위치의 경로 확인
		Path currentRelativePath = Paths.get("");
		String point = currentRelativePath.toAbsolutePath().toString();
		
		System.out.println("start");	
		System.out.println("> " + point);
		
		try {
			
			// 프로그램이 실행되는 위치의 폴더 확인
			DirectoryStream.Filter<Path> filter = new DirectoryStream.Filter<Path>() {
		        @Override
		        public boolean accept(Path file) throws IOException {
		            return (Files.isDirectory(file));
		        }
		    };

		    // 해당 폴더 별로 resize, rename 요청
		    Path dir = FileSystems.getDefault().getPath(point);
		    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
		        for (Path path : stream) {
		            System.out.println(path);
		            irp.proc(path.toString(), point, path.getFileName().toString());
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		        System.err.println(e);
		    }
			
		} catch(Exception e) {
		    e.printStackTrace();
		    System.err.println(e);
		}
		System.out.println("end");
	}
}