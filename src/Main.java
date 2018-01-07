import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;


public class Main {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] mainColor = {57,57,99}; //角色特征像素
		int fixY = 300; //屏蔽Y轴计分板
		while(true){
			try {
				CetImage(); //截取屏幕图像到电脑
				int distance = getDistance(mainColor,fixY); //获取角色到目标点的距离
				System.out.println("Distance:"+distance);
			    RunCmd("cmd /c G:/SDK/android-sdk-windows/platform-tools/adb shell input swipe 200 200 200 200 "+distance); //adb模拟点击，开始跳跃
				Thread.sleep(1400); //线程休眠，等待画面稳定
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static int getDistance(int[] mainColor,int fixY) throws IOException{
		File file = new File("D:/tmp.png"); //图片路径
		boolean STP = true;
		boolean ETP = true;
		int x1 = 0,y1 = 0,x2 = 0,y2 = 0;
		BufferedImage image = ImageIO.read(file); 
		int tmp = getIndex(image,0,fixY);
	    for (int i = fixY;i<1920;i++){
	        for(int j = 0;j<1080;j++){
	        	if(Math.abs(getIndex(image,j,i)-tmp)>30&&ETP){//简单比较特征
	        		System.out.println("Starting Point:"+j+","+i);
	        		x1 = j;
	        		y1 = i;
	        		ETP = false;
	        	}
	        	if(Arrays.equals(getRGB(image,j,i), mainColor)&&STP){
		        	System.out.println("End Point:"+j+","+i);
		        	x2 = j;
		        	y2 = i;
		        	STP = false;
	        	}
	        }
	    } 
		return (int)(Math.sqrt((Math.abs(x1 - x2)*Math.abs(x1 - x2))+(Math.abs(y1 - y2)*Math.abs(y1 - y2)))*1.2);//通过坐标计算距离
	}
	
	public static int[] getRGB(BufferedImage image,int x,int y){ //根据坐标获取像素
		int[] rgb = new int [3];
		int pixel = image.getRGB(x,y);
		rgb[0] = (pixel & 0xff0000) >> 16;
        rgb[1] = (pixel & 0xff00) >> 8;
        rgb[2] = (pixel & 0xff);
        return rgb;
	}
	
	public static int getIndex(BufferedImage image,int x,int y){ //根据坐标获取特征
		int[] rgb = getRGB(image,x,y);
        return rgb[0]+rgb[1]+rgb[2];
	}
	
	public static void CetImage(){
		RunCmd("cmd /c G:/SDK/android-sdk-windows/platform-tools/adb shell /system/bin/screencap -p /sdcard/tmp.png"); //调用adb截图，储存到sd卡
		RunCmd("cmd /c G:/SDK/android-sdk-windows/platform-tools/adb pull /sdcard/tmp.png D:/tmp.png"); //调用adb将图片赋值到电脑 
	}
	
	public static void RunCmd(String cmd){ //执行命令
		try { 
			Process process = null;
			process = Runtime.getRuntime().exec(cmd);
			process.waitFor(); 
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
