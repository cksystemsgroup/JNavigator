
package at.uni_salzburg.cs.ckgroup.pilot.sensor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class GoogleMapsCameraTestCase {

	
//	@Test
	public void testCase01 () {
		
		long zoom = 18;
		
		double lat_deg = 47.82202396;
		double lon_deg = 13.04324663;
		
//		double lat_deg = 47.82201946;
//		double lon_deg = 13.04082647;
		
		double lat_rad = Math.toRadians(lat_deg);
		
		long n = (long) Math.pow(2, zoom);
		double xtile = ((lon_deg + 180.0) / 360.0) * n;
		double ytile = (1.0 - (Math.log(Math.tan(lat_rad) + 1.0/Math.cos(lat_rad)) / Math.PI)) / 2.0 * n;
		double px = 256.0 * (xtile - (long)xtile);
		double py = 256.0 * (ytile - (long)ytile);
		System.out.printf("Zoom %d, lat %.8f, lon %.8f, x=%.4f, y=%.4f, px=%.0f, py=%.0f\n",zoom, lat_deg, lon_deg, xtile, ytile, px, py);

//		http://khm0.google.at/kh/v=80&x=[ TILE-X ]&y=[ TILE-Y ]&z=[ ZOOM ]
//		http://khm0.google.at/kh/v=80&x=140568&y=91318&z=18
//		http://b.tile.opencyclemap.org/cycle/18/140568/91318.png
	}
	
//	@Test
	public void testCase02 () {
		
		long zoom = 19;
		double lat_deg = 47.82202396;
		double lon_deg = 13.04324663;
		
//		double lat_deg = 47.82201946;
//		double lon_deg = 13.04082647;
		
		double lat_rad = Math.toRadians(lat_deg);
		
		long n = (long) Math.pow(2, zoom);
		double xtile = ((lon_deg + 180.0) / 360.0) * n;
		double ytile = (1.0 - (Math.log(Math.tan(lat_rad) + 1.0/Math.cos(lat_rad)) / Math.PI)) / 2.0 * n;
		double px = 256.0 * (xtile - (long)xtile);
		double py = 256.0 * (ytile - (long)ytile);
		System.out.printf("Zoom %d, lat %.8f, lon %.8f, x=%.4f, y=%.4f, px=%.0f, py=%.0f\n",zoom, lat_deg, lon_deg, xtile, ytile, px, py);

//		http://khm0.google.at/kh/v=80&x=[ TILE-X ]&y=[ TILE-Y ]&z=[ ZOOM ]
//		http://khm0.google.at/kh/v=80&x=281136&y=182636&z=19
//		http://khm0.google.at/kh/v=80&x=281139&y=182636&z=19
	}
	
//	@Test
	public void testCase03 () {
		
		long zoom = 19;
		
		double lat_deg = 47.82201946;
		double lon_deg = 13.04082647;
		
//		double lat_deg = 47.82201946;
//		double lon_deg = 13.04082647;
		
		double lat_rad = Math.toRadians(lat_deg);
		
		long n = (long) Math.pow(2, zoom);
		double xtile = ((lon_deg + 180.0) / 360.0) * n;
		double ytile = (1.0 - (Math.log(Math.tan(lat_rad) + 1.0/Math.cos(lat_rad)) / Math.PI)) / 2.0 * n;
		double px = 256.0 * (xtile - (long)xtile);
		double py = 256.0 * (ytile - (long)ytile);
		System.out.printf("Zoom %d, lat %.8f, lon %.8f, x=%.4f, y=%.4f, px=%.0f, py=%.0f\n",zoom, lat_deg, lon_deg, xtile, ytile, px, py);

//		http://khm0.google.at/kh/v=80&x=[ TILE-X ]&y=[ TILE-Y ]&z=[ ZOOM ]
//		http://khm0.google.at/kh/v=80&x=281136&y=182636&z=19
//		http://khm0.google.at/kh/v=80&x=281136&y=182636&z=19
	}
	
	
//	@Test
	public void testCase04() throws IOException {

//		URL url1 = new URL("http://a.tile.opencyclemap.org/cycle/20/562271/365272.png");
//		URL url2 = new URL("http://b.tile.opencyclemap.org/cycle/20/562272/365273.png");
//		URL url3 = new URL("http://c.tile.opencyclemap.org/cycle/20/562273/365274.png");
		URL url1 = new URL("http://a.tile.opencyclemap.org/cycle/18/140567/91317.png");
		URL url2 = new URL("http://b.tile.opencyclemap.org/cycle/18/140568/91317.png");
		URL url3 = new URL("http://c.tile.opencyclemap.org/cycle/18/140569/91317.png");
		URL url4 = new URL("http://a.tile.opencyclemap.org/cycle/18/140567/91318.png");
		URL url5 = new URL("http://b.tile.opencyclemap.org/cycle/18/140568/91318.png");
		URL url6 = new URL("http://c.tile.opencyclemap.org/cycle/18/140569/91318.png");
		URL url7 = new URL("http://a.tile.opencyclemap.org/cycle/18/140567/91319.png");
		URL url8 = new URL("http://b.tile.opencyclemap.org/cycle/18/140568/91319.png");
		URL url9 = new URL("http://c.tile.opencyclemap.org/cycle/18/140569/91319.png");
//		URL url1 = new URL("http://otile1.mqcdn.com/tiles/1.0.0/osm/20/562271/365272.png");
//		URL url2 = new URL("http://otile1.mqcdn.com/tiles/1.0.0/osm/20/562272/365273.png");
//		URL url3 = new URL("http://otile1.mqcdn.com/tiles/1.0.0/osm/20/562273/365274.png");
//		URL url1 = new URL("http://otile1.mqcdn.com/tiles/1.0.0/osm/19/281136/182636.png");
//		URL url2 = new URL("http://otile1.mqcdn.com/tiles/1.0.0/osm/19/281136/182636.png");
//		URL url3 = new URL("http://otile1.mqcdn.com/tiles/1.0.0/osm/19/281136/182636.png");
//		URL url1 = new URL("http://otile1.mqcdn.com/tiles/1.0.0/osm/18/140567/91317.png");
//		URL url2 = new URL("http://otile1.mqcdn.com/tiles/1.0.0/osm/18/140568/91318.png");
//		URL url3 = new URL("http://otile1.mqcdn.com/tiles/1.0.0/osm/18/140569/91319.png");
		
		// OK:
//		URL url1 = new URL("http://a.tile.opencyclemap.org/cycle/17/70283/45658.png");
//		URL url2 = new URL("http://b.tile.opencyclemap.org/cycle/17/70284/45658.png");
//		URL url3 = new URL("http://c.tile.opencyclemap.org/cycle/17/70285/45658.png");
//		URL url4 = new URL("http://tile.opencyclemap.org/cycle/17/70283/45659.png");
//		URL url5 = new URL("http://tile.opencyclemap.org/cycle/17/70284/45659.png");
//		URL url6 = new URL("http://tile.opencyclemap.org/cycle/17/70285/45659.png");
//		URL url7 = new URL("http://tile.opencyclemap.org/cycle/17/70283/45660.png");
//		URL url8 = new URL("http://tile.opencyclemap.org/cycle/17/70284/45660.png");
//		URL url9 = new URL("http://tile.opencyclemap.org/cycle/17/70285/45660.png");
		
//		URL url5 = new URL("http://otile1.mqcdn.com/tiles/1.0.0/osm/17/70284/45659.png");
//		URL url9 = new URL("http://otile1.mqcdn.com/tiles/1.0.0/osm/17/70285/45660.png");
//        "http://otile1.mqcdn.com/tiles/1.0.0/osm/${z}/${x}/${y}.png",
//        "http://otile2.mqcdn.com/tiles/1.0.0/osm/${z}/${x}/${y}.png",
//        "http://otile3.mqcdn.com/tiles/1.0.0/osm/${z}/${x}/${y}.png",
//        "http://otile4.mqcdn.com/tiles/1.0.0/osm/${z}/${x}/${y}.png"],

		BufferedImage input1 = ImageIO.read(url1);
		BufferedImage input2 = ImageIO.read(url2);
		BufferedImage input3 = ImageIO.read(url3);
		BufferedImage input4 = ImageIO.read(url4);
		BufferedImage input5 = ImageIO.read(url5);
		BufferedImage input6 = ImageIO.read(url6);
		BufferedImage input7 = ImageIO.read(url7);
		BufferedImage input8 = ImageIO.read(url8);
		BufferedImage input9 = ImageIO.read(url9);
		
		BufferedImage im = new BufferedImage(3*256,3*256,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = im.createGraphics(); 
//		g2d.drawImage(input,0*256,0*256,null);
//		g2d.drawImage(input,0*256,1*256,null);
//		g2d.drawImage(input,0*256,2*256,null);
//		g2d.drawImage(input,1*256,0*256,null);
//		g2d.drawImage(input,1*256,1*256,null);
//		g2d.drawImage(input,1*256,2*256,null);
//		g2d.drawImage(input,2*256,0*256,null);
//		g2d.drawImage(input,2*256,1*256,null);
//		g2d.drawImage(input,2*256,2*256,null);
//		ImageIO.write(im,"PNG",new File("rendered_lena.png"));
		
		Graphics ctile1 = g2d.create(0*256, 0*256, 256, 256);
		Graphics ctile2 = g2d.create(1*256, 0*256, 256, 256);
		Graphics ctile3 = g2d.create(2*256, 0*256, 256, 256);
		Graphics ctile4 = g2d.create(0*256, 1*256, 256, 256);
		Graphics ctile5 = g2d.create(1*256, 1*256, 256, 256);
		Graphics ctile6 = g2d.create(2*256, 1*256, 256, 256);
		Graphics ctile7 = g2d.create(0*256, 2*256, 256, 256);
		Graphics ctile8 = g2d.create(1*256, 2*256, 256, 256);
		Graphics ctile9 = g2d.create(2*256, 2*256, 256, 256);
		
		ctile1.drawImage(input1,0,0,null);
		ctile2.drawImage(input2,0,0,null);		
		ctile3.drawImage(input3,0,0,null);
		ctile4.drawImage(input4,0,0,null);
		ctile5.drawImage(input5,0,0,null);
		ctile6.drawImage(input6,0,0,null);
		ctile7.drawImage(input7,0,0,null);
		ctile8.drawImage(input8,0,0,null);
		ctile9.drawImage(input9,0,0,null);
		ImageIO.write(im,"PNG",new File("rendered_lena.png"));
		
		BufferedImage sim1 = im.getSubimage(30, 50, 256, 256);		
		ImageIO.write(sim1,"PNG",new File("rendered_lena2.png"));
		
		BufferedImage sim2 = im.getSubimage(40, 50, 256, 256);		
		ImageIO.write(sim2,"PNG",new File("rendered_lena3.png"));
		
		BufferedImage sim3 = im.getSubimage(50, 50, 256, 256);		
		ImageIO.write(sim3,"PNG",new File("rendered_lena4.png"));
		
		BufferedImage sim4 = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB);
		Graphics2D gc = sim4.createGraphics();
		gc.drawImage(sim3.getScaledInstance(600, 600, 0),0,0,null);
		gc.setPaint(Color.RED);
		gc.fillOval(300, 300, 10, 10);
		ImageIO.write(sim4,"PNG",new File("rendered_lena5.png"));
		
//		ByteArrayOutputStream os = new ByteArrayOutputStream();
//		ImageIO.write(sim4, "PNG", os);
		
		System.out.println("done!");		
	}
	
	
//	@Test
	public void testCase15() throws IOException {

		URL url1 = new URL("http://khm0.google.at/kh/v=80&x=281135&y=182635&z=19");
		URL url2 = new URL("http://khm0.google.at/kh/v=80&x=281136&y=182635&z=19");
		URL url3 = new URL("http://khm0.google.at/kh/v=80&x=281137&y=182635&z=19");
		URL url4 = new URL("http://khm0.google.at/kh/v=80&x=281135&y=182636&z=19");
		URL url5 = new URL("http://khm0.google.at/kh/v=80&x=281136&y=182636&z=19");
		URL url6 = new URL("http://khm0.google.at/kh/v=80&x=281137&y=182636&z=19");
		URL url7 = new URL("http://khm0.google.at/kh/v=80&x=281135&y=182637&z=19");
		URL url8 = new URL("http://khm0.google.at/kh/v=80&x=281136&y=182637&z=19");
		URL url9 = new URL("http://khm0.google.at/kh/v=80&x=281137&y=182637&z=19");

		BufferedImage input1 = ImageIO.read(url1);
		BufferedImage input2 = ImageIO.read(url2);
		BufferedImage input3 = ImageIO.read(url3);
		BufferedImage input4 = ImageIO.read(url4);
		BufferedImage input5 = ImageIO.read(url5);
		BufferedImage input6 = ImageIO.read(url6);
		BufferedImage input7 = ImageIO.read(url7);
		BufferedImage input8 = ImageIO.read(url8);
		BufferedImage input9 = ImageIO.read(url9);
		
		BufferedImage im = new BufferedImage(3*256,3*256,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = im.createGraphics(); 
		
		Graphics ctile1 = g2d.create(0*256, 0*256, 256, 256);
		Graphics ctile2 = g2d.create(1*256, 0*256, 256, 256);
		Graphics ctile3 = g2d.create(2*256, 0*256, 256, 256);
		Graphics ctile4 = g2d.create(0*256, 1*256, 256, 256);
		Graphics ctile5 = g2d.create(1*256, 1*256, 256, 256);
		Graphics ctile6 = g2d.create(2*256, 1*256, 256, 256);
		Graphics ctile7 = g2d.create(0*256, 2*256, 256, 256);
		Graphics ctile8 = g2d.create(1*256, 2*256, 256, 256);
		Graphics ctile9 = g2d.create(2*256, 2*256, 256, 256);
		
		ctile1.drawImage(input1,0,0,null);
		ctile2.drawImage(input2,0,0,null);		
		ctile3.drawImage(input3,0,0,null);
		ctile4.drawImage(input4,0,0,null);
		ctile5.drawImage(input5,0,0,null);
		ctile6.drawImage(input6,0,0,null);
		ctile7.drawImage(input7,0,0,null);
		ctile8.drawImage(input8,0,0,null);
		ctile9.drawImage(input9,0,0,null);
		ImageIO.write(im,"PNG",new File("rendered_lena.png"));
		
		BufferedImage sim1 = im.getSubimage(30, 50, 256, 256);		
		ImageIO.write(sim1,"PNG",new File("rendered_lena2.png"));
		
		BufferedImage sim2 = im.getSubimage(40, 50, 256, 256);		
		ImageIO.write(sim2,"PNG",new File("rendered_lena3.png"));
		
		BufferedImage sim3 = im.getSubimage(50, 50, 256, 256);		
		ImageIO.write(sim3,"PNG",new File("rendered_lena4.png"));
		
		BufferedImage sim4 = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB);
		Graphics2D gc = sim4.createGraphics();
		gc.drawImage(sim3.getScaledInstance(600, 600, 0),0,0,null);
		gc.setPaint(Color.RED);
		gc.fillOval(300, 300, 10, 10);
		ImageIO.write(sim4,"PNG",new File("rendered_lena5.png"));
		
		System.out.println("done!");		
	}
}
