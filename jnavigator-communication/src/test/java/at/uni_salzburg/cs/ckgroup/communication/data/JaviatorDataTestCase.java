package at.uni_salzburg.cs.ckgroup.communication.data;

import junit.framework.TestCase;

public class JaviatorDataTestCase extends TestCase {

	public void testCase01() {
		byte payload[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
				14, 15, 16, 17, 18,	19, 20, 21, 22, 23, 24, 25, 26,
				27, 28, 29, 30, 31, 32, 33, 34, 35, 36,	37, 38, 39,
				40, 41, 42, 43, 44, 45, 46, 47, 48 };
		
		JaviatorData jd = new JaviatorData (payload);
		
		byte[] b2 = jd.toByteArray();
		assertEquals("Array length differs", payload.length, b2.length);

		for (int k = 0; k < payload.length; k++)
			assertEquals("Array index " + k, payload[k], b2[k]);
	}
	
	public void testCase02() {
		byte payload[] = {
				 -1,  -2,  -3,  -4,  -5,  -6,  -7,  -8,  -9, -10, -11, -12, -13,
				-14, -15, -16, -17, -18, -19, -20, -21, -22, -23, -24, -25, -26,
				-27, -28, -29, -30, -31, -32, -33, -34, -35, -36, -37, -38, -39,
				-40, -41, -42, -43, -44, -45, -46, -47, -48 };
		
		JaviatorData jd = new JaviatorData (payload);
		
		byte[] b2 = jd.toByteArray();
		assertEquals("Array length differs", payload.length, b2.length);

		for (int k = 0; k < payload.length; k++)
			assertEquals("Array index " + k, payload[k], b2[k]);
	}
	
	
	public void testCase03() {
		byte payload[] = {
				  1,  -2,   3,  -4,   5,  -6,   7,  -8,   9, -10,  11, -12,  13,
				-14,  15, -16,  17, -18,  19, -20,  21, -22,  23, -24,  25, -26,
				 27, -28,  29, -30,  31, -32,  33, -34,  35, -36,  37, -38,  39,
				-40,  41, -42,  43, -44,  45, -46,  47, -48 };
		
		JaviatorData jd = new JaviatorData (payload);
		
		byte[] b2 = jd.toByteArray();
		assertEquals("Array length differs", payload.length, b2.length);

		for (int k = 0; k < payload.length; k++)
			assertEquals("Array index " + k, payload[k], b2[k]);
	}
	
	
	public void testCase04() {
		byte payload[] = {
				 -1,   2,  -3,   4,  -5,   6,  -7,   8,  -9,  10, -11,  12, -13,
				 14, -15,  16, -17,  18, -19,  20, -21,  22, -23,  24, -25,  26,
				-27,  28, -29,  30, -31,  32, -33,  34, -35,  36, -37,  38, -39,
				 40, -41,  42, -43,  44, -45,  46, -47,  48 };
		
		JaviatorData jd = new JaviatorData (payload);
		
		byte[] b2 = jd.toByteArray();
		assertEquals("Array length differs", payload.length, b2.length);

		for (int k = 0; k < payload.length; k++)
			assertEquals("Array index " + k, payload[k], b2[k]);
	}
	
	public void testCase05() {
		
		JaviatorData jd = new JaviatorData ();
		jd.setMaps(12345.6789);
		jd.setTemp(12.3456);
		jd.setBatt(13.7654);
		jd.setSonar(10.9876);
		jd.setState(1331);
		jd.setId(31337);
		jd.setX_pos(12345.6789);
		jd.setY_pos(98765.4321);
		jd.setRoll(2.456789);
		jd.setPitch(-3.123987);
		jd.setYaw(1.159753);
		jd.setDroll(0.12345);
		jd.setDpitch(1.012345);
		jd.setDyaw(-1.342512);
		jd.setDdx(-0.987654);
		jd.setDdy(1.234576);
		jd.setDdz(0.334455);

//		assertEquals("JaviatorData: maps=12345678, temp=12345, batt=13765, sonar=10987, state=1331, id=31337, x_pos=12345678, y_pos=98765432, roll=5456, pitch=3123, yaw=17159, droll=123, dpitch=1012, dyaw=-1342, ddx=-987, ddy=1234, ddz=334", jd.toString());
//		assertEquals("JaviatorData: maps=1801, temp=-281, batt=783, sonar=3750, state=1331, id=31337, x_pos=12345678, y_pos=98765432, roll=-8620, pitch=32584, yaw=-17626, droll=475, dpitch=3902, dyaw=-5175, ddx=-471, ddy=589, ddz=159", jd.toString());
		
		assertEquals(12345.678, jd.getMaps(),   1E-0);
		assertEquals(   12.345, jd.getTemp(),   1E-1);
		assertEquals(   13.765, jd.getBatt(),   1E-2);
		assertEquals(   10.987, jd.getSonar(),  1E-2);
		assertEquals(     1331, jd.getState());
		assertEquals(    31337, jd.getId());
		assertEquals(12345.678, jd.getX_pos(),  1E-9);
		assertEquals(98765.432, jd.getY_pos(),  1E-9);
		assertEquals(    2.456, jd.getRoll(),   1E-3);
		assertEquals(   -3.123, jd.getPitch(),  1E-3);
		assertEquals(    1.159, jd.getYaw(),    1E-3);
		assertEquals(    0.123, jd.getDroll(),  1E-3);
		assertEquals(    1.012, jd.getDpitch(), 1E-3);
		assertEquals(   -1.342, jd.getDyaw(),   1E-3);
		assertEquals(   -0.987, jd.getDdx(),    1E-3);
		assertEquals(    1.234, jd.getDdy(),    1E-3);
		assertEquals(    0.334, jd.getDdz(),    1E-3);
		
		
		jd.setX_pos(  125.6789);
		jd.setY_pos(    9.4321);
		assertEquals( 125.678, jd.getX_pos(),   1E-9);
		assertEquals(   9.432, jd.getY_pos(),   1E-9);
		System.out.println("jd="+jd.toString());
	}
}
