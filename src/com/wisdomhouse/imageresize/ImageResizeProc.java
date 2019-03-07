package com.wisdomhouse.imageresize;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class ImageResizeProc {
	static final int _HEIGHT = 1080;
	static final int _WIDTH = 690;
	static final String _IMAGE_EXT = "jpg";
	
	public void proc(String procPath, String originPath, String originFolder) {
		int maxWidth = 0;			// 원본 파일의 max 너비
		int originalSumHeight = 0;  // 원본 파일 높이 합산
		
		List<String> pathList = new ArrayList<String>();		// 원본 이미지 폴더의 이미지 파일 경로 정보
		List<Integer> heightList = new ArrayList<Integer>();	// 원본 이미지 폴더의 높이 정보
		
		try {
			// 해당 폴더의 이미지 파일을 읽어 각각 원본 이미지 파일 경로와, 원본 이미지 파일들의 각각의 높이 및 합산 높이를 구함
			File[] files = this.getFileNames(procPath, _IMAGE_EXT);
			
			for(File file : files) {
				String filePath = file.getPath().toString();
				System.out.println("  "+filePath);
				pathList.add(filePath);
				BufferedImage image = null;
				try {
					image = ImageIO.read(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				maxWidth = Math.max(image.getWidth(), maxWidth);
				originalSumHeight += image.getHeight();
				heightList.add(image.getHeight());
				
				
			}
			
			// 이미지 합치기 작업
			BufferedImage mergedImage = new BufferedImage(maxWidth, originalSumHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = (Graphics2D) mergedImage.getGraphics();
			graphics.setBackground(Color.WHITE);
			
			int getHeight = 0;
			int cnt = 0;
			for(String filePath : pathList) {
				File file = new File(filePath);
				try {
					BufferedImage image = ImageIO.read(file);
					graphics.drawImage(image, 0, getHeight, null);
					getHeight += heightList.get(cnt++);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
			// 합쳐진 이미지 자르고 리사이즈 작업
			int height = _HEIGHT;
			int cropHeight = 0;
			
			String resizeFilePath = originPath+"/resize/"+originFolder+"/";
			File dir = new File(resizeFilePath);
			if(!dir.exists()) {
				dir.mkdirs();
			}
			
			double ratio; // 비율
			int w;		  // 변하게 할 가로 사이즈
			
			ratio = (double)_WIDTH/(double)maxWidth;
			w = (int)(maxWidth*ratio);
			
			String imageFileName = "";
			
			for(int i=0; i<(originalSumHeight/_HEIGHT)+1; i++) {
				imageFileName = numberToString(i+1) + "." + _IMAGE_EXT;
				
				if(originalSumHeight/_HEIGHT == i) { // 마지막 남은 이미지는 작을 수도 있음
					height = originalSumHeight%_HEIGHT; 
					System.out.println("  last height : " + height);
				}
				BufferedImage bufferedImage = new BufferedImage(maxWidth, height, BufferedImage.TYPE_INT_BGR);
				bufferedImage.createGraphics().drawImage(mergedImage, 0, cropHeight, null);
				cropHeight -= _HEIGHT;	// 잘리는 높이는 - 좌표
				
				
				Image resizeImage = bufferedImage.getScaledInstance(w, height, Image.SCALE_SMOOTH);
				BufferedImage newImage = new BufferedImage(w, height, BufferedImage.TYPE_INT_RGB);
				Graphics g = newImage.getGraphics();
				g.drawImage(resizeImage, 0, 0, null);
				g.dispose();
				ImageIO.write(newImage, _IMAGE_EXT, new File(resizeFilePath + imageFileName));
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println(e);
		}
	}
	
	// 해당 폴더에 원하는 확장자의 file 값만 구해서 리턴
	private File[] getFileNames(String targetDirName, String fileExt) {
		File dir = new File(targetDirName);

		File[] files = null;
		if (dir.isDirectory()) {
			final String ext = fileExt.toLowerCase();
			files = dir.listFiles(new FileFilter() {
				public boolean accept(File file) {
					if (file.isDirectory()) {
						return false;
					}
					return file.getName().toLowerCase().endsWith("." + ext);
				}
			});
		}

		return files;
	}
	
	// resize+rename 된 최종본의 파일명 세자리 세팅
	public static String numberToString(int n) {
		if(n < 10) {
			return "00"+n;
		} else if ( 10 <= n && n < 100 ) {
			return "0"+n;
		} else {
			return ""+n;
		}
	}
}