package com.dianping.puma.integration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.puma.core.event.ChangedEvent;
import com.dianping.puma.core.event.RowChangedEvent;

public class DataTypeIntegrationTest extends PumaServerIntegrationBaseTest {
	private String	table	= "dataTypeTest";

	@Before
	public void before() throws Exception {
		executeSql("DROP TABLE IF EXISTS " + table);
	}

	@Test
	public void testBigInt() throws Exception {
		executeSql("CREATE TABLE " + table + "(id BIGINT)");
		executeSql("INSERT INTO " + table + " values(1)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id=9223372036854775807 WHERE id=1");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof BigInteger);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue()
						.equals(BigInteger.valueOf(9223372036854775807L)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(BigInteger.ONE));
			}
		});
	}

	@Test
	public void testBigInt2() throws Exception {
		executeSql("CREATE TABLE " + table + "(id BIGINT)");
		executeSql("INSERT INTO " + table + " values(-9223372036854775808)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id=9223372036854775807 WHERE id=-9223372036854775808");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof BigInteger);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue()
						.equals(BigInteger.valueOf(9223372036854775807L)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue()
						.equals(new BigInteger("-9223372036854775808")));
			}
		});
	}

	@Test
	public void testBigIntUnsgined() throws Exception {
		executeSql("CREATE TABLE " + table + "(id BIGINT UNSIGNED)");
		executeSql("INSERT INTO " + table + " values(1)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id=9223372036854775808 WHERE id=1");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof BigInteger);
				Assert.assertTrue(((BigInteger) rowChangedEvent.getColumns().get("id").getNewValue()).toString()
						.equals("9223372036854775808"));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(BigInteger.ONE));
			}
		});
	}

	@Test
	public void testBinary() throws Exception {
		executeSql("CREATE TABLE " + table + "(id BINARY(8))");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				byte[] data = new byte[] { 1, 2, 3, 54, 5, 6, 67 };
				insertWithBinaryColumn("INSERT INTO " + table + " values(?)", data);
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue() instanceof String);
				Assert.assertEquals(new String(data), (String) rowChangedEvent.getColumns().get("id").getNewValue());
			}
		});
	}

	@Test
	public void testVarBinary() throws Exception {
		executeSql("CREATE TABLE " + table + "(id VARBINARY(10))");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				byte[] data = new byte[] { 1, 2, 3, 54, 5, 6, 67 };
				insertWithBinaryColumn("INSERT INTO " + table + " values(?)", data);
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue() instanceof String);
				Assert.assertEquals(new String(data), (String) rowChangedEvent.getColumns().get("id").getNewValue());
			}
		});
	}

	@Test
	public void testBit() throws Exception {
		executeSql("CREATE TABLE " + table + "(id BIT(8))");
		executeSql("INSERT INTO " + table + " values(11)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id=10 WHERE id=11");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof byte[]);
				Assert.assertArrayEquals(new byte[] { 11 }, (byte[]) rowChangedEvent.getColumns().get("id")
						.getOldValue());
				Assert.assertArrayEquals(new byte[] { 10 }, (byte[]) rowChangedEvent.getColumns().get("id")
						.getNewValue());
			}
		});
	}

	@Test
	public void testBool() throws Exception {
		executeSql("CREATE TABLE " + table + "(id BOOL)");
		executeSql("INSERT INTO " + table + " values(11)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id=10 WHERE id=11");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Integer);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new Integer(11)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Integer(10)));
			}
		});
	}

	@Test
	public void testBoolean() throws Exception {
		executeSql("CREATE TABLE " + table + "(id BOOLEAN)");
		executeSql("INSERT INTO " + table + " values(11)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id=10 WHERE id=11");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Integer);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new Integer(11)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Integer(10)));
			}
		});
	}

	@Test
	public void testChar() throws Exception {
		executeSql("CREATE TABLE " + table + "(id CHAR)");
		executeSql("INSERT INTO " + table + " values('a')");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id='b' WHERE id='a'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof String);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new String("a")));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new String("b")));
			}
		});
	}

	@Test
	public void testDate() throws Exception {
		executeSql("CREATE TABLE " + table + "(id DATE)");
		executeSql("INSERT INTO " + table + " values('2012-01-1')");
		waitForSync(50);
		test(new TestLogic() {

			@SuppressWarnings("deprecation")
			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id='2013-09-11' WHERE id='2012-01-01'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Date);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = sdf.parse("2012-01-01");
				Date oldvalue = (Date) rowChangedEvent.getColumns().get("id").getOldValue();
				Assert.assertEquals(date.getDate(), oldvalue.getDate());
				Assert.assertEquals(date.getMonth(), oldvalue.getMonth());
				Assert.assertEquals(date.getYear(), oldvalue.getYear());
				Date newdate = sdf.parse("2013-09-11");
				Date newvalue = (Date) rowChangedEvent.getColumns().get("id").getNewValue();
				Assert.assertEquals(newdate.getDate(), newvalue.getDate());
				Assert.assertEquals(newdate.getMonth(), newvalue.getMonth());
				Assert.assertEquals(newdate.getYear(), newvalue.getYear());
			}
		});
	}

	@Test
	public void testDateTime() throws Exception {
		executeSql("CREATE TABLE " + table + "(id DATETIME)");
		executeSql("INSERT INTO " + table + " values('2009-9-9 23:22:11')");
		waitForSync(50);
		test(new TestLogic() {
			@SuppressWarnings("deprecation")
			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id='2010-10-10 22:11:22' WHERE id='2009-9-9 23:22:11'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Date);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = sdf.parse("2009-9-9 23:22:11");
				Date oldvalue = (Date) rowChangedEvent.getColumns().get("id").getOldValue();
				Assert.assertEquals(date.getDate(), oldvalue.getDate());
				Assert.assertEquals(date.getMonth(), oldvalue.getMonth());
				Assert.assertEquals(date.getYear(), oldvalue.getYear());
				Assert.assertEquals(date.getHours(), oldvalue.getHours());
				Assert.assertEquals(date.getMinutes(), oldvalue.getMinutes());
				Assert.assertEquals(date.getSeconds(), oldvalue.getSeconds());
				Date newdate = sdf.parse("2010-10-10 22:11:22");
				Date newvalue = (Date) rowChangedEvent.getColumns().get("id").getNewValue();
				Assert.assertEquals(newdate.getDate(), newvalue.getDate());
				Assert.assertEquals(newdate.getMonth(), newvalue.getMonth());
				Assert.assertEquals(newdate.getYear(), newvalue.getYear());
				Assert.assertEquals(newdate.getHours(), newvalue.getHours());
				Assert.assertEquals(newdate.getMinutes(), newvalue.getMinutes());
				Assert.assertEquals(newdate.getSeconds(), newvalue.getSeconds());
			}

		});
	}

	@Test
	public void testDecimal() throws Exception {
		executeSql("CREATE TABLE " + table + "(id DECIMAL(4,2))");
		executeSql("INSERT INTO " + table + " values(99.99)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id=0 WHERE id=99.99");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof BigDecimal);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new BigDecimal("99.99")));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new BigDecimal("0.00")));

			}
		});
	}

	@Test
	public void testDouble() throws Exception {
		executeSql("CREATE TABLE " + table + "(id DOUBLE )");
		executeSql("INSERT INTO " + table + " values(2.2250738585072E-308)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id=4.56 WHERE id=2.2250738585072E-308");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Double);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue()
						.equals(new Double(2.2250738585072E-308)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Double(4.56)));

			}
		});
	}

	@Test
	public void testEnum() throws Exception {
		executeSql("CREATE TABLE " + table + "(id ENUM('one','two','three'))");
		executeSql("INSERT INTO " + table + " values('two')");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id='three' WHERE id='two'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Integer);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new Integer(2)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Integer(3)));

			}
		});
	}

	@Test
	public void testFloat() throws Exception {
		executeSql("CREATE TABLE " + table + "(id INT, val FLOAT )");
		executeSql("INSERT INTO " + table + " (id, val) VALUES (1, -3.40282E+38)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET val=4.56 WHERE id=1");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("val").getOldValue() instanceof Float);
				Assert.assertTrue(rowChangedEvent.getColumns().get("val").getOldValue().equals(new Float(-3.40282E+38)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("val").getNewValue().equals(new Float(4.56)));

			}
		});
	}

	@Test
	public void testReal() throws Exception {
		executeSql("CREATE TABLE " + table + "(id REAL )");
		executeSql("INSERT INTO " + table + " values(2.2250738585072E-308)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id=4.56 WHERE id=2.2250738585072E-308");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Double);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue()
						.equals(new Double(2.2250738585072E-308)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Double(4.56)));

			}
		});
	}

	@Test
	public void testInt() throws Exception {
		executeSql("CREATE TABLE " + table + "(id INT)");
		executeSql("INSERT INTO " + table + " values(" + Integer.MAX_VALUE + ")");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id=" + Integer.MIN_VALUE + " WHERE id=" + Integer.MAX_VALUE);
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Long);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(-2147483648L));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(2147483647L));
			}
		});
	}

	@Test
	public void testIntUnsigned() throws Exception {
		executeSql("CREATE TABLE " + table + "(id INT UNSIGNED)");
		executeSql("INSERT INTO " + table + " values(2147483648)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id=1 WHERE id=2147483648");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Long);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(1L));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(2147483648L));
			}
		});
	}

	@Test
	public void testMediumInt() throws Exception {
		executeSql("CREATE TABLE " + table + "(id MEDIUMINT)");
		executeSql("INSERT INTO " + table + " values(-8388608)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id= 8388607 WHERE id=-8388608");
				executeSql("UPDATE " + table + " SET id= -3456  WHERE id=8388607");
				List<ChangedEvent> events = getEvents(2, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Integer);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Integer(8388607)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new Integer(-8388608)));

				rowChangedEvent = (RowChangedEvent) events.get(1);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Integer);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new Integer(8388607)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Integer(-3456)));
			}
		});
	}

	@Test
	public void testMediumIntUnsigned() throws Exception {
		executeSql("CREATE TABLE " + table + "(id MEDIUMINT UNSIGNED)");
		executeSql("INSERT INTO " + table + " values(0)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id= 8388608 WHERE id=0");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Integer);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Integer(8388608)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new Integer(0)));

			}
		});
	}

	@Test
	public void testSmallInt() throws Exception {
		executeSql("CREATE TABLE " + table + "(id SMALLINT)");
		executeSql("INSERT INTO " + table + " values(-32768)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id= 32767 WHERE id=-32768");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Integer);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Integer(32767)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new Integer(-32768)));

			}
		});
	}

	@Test
	public void testSmallIntUnsigned() throws Exception {
		executeSql("CREATE TABLE " + table + "(id SMALLINT UNSIGNED)");
		executeSql("INSERT INTO " + table + " values(0)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id= 32768 WHERE id=0");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Integer);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Integer(32768)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new Integer(0)));

			}
		});
	}

	@Test
	public void testTinyInt() throws Exception {
		executeSql("CREATE TABLE " + table + "(id TINYINT)");
		executeSql("INSERT INTO " + table + " values(127)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id= -128 WHERE id=127");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Integer);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Integer(-128)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new Integer(127)));

			}
		});
	}

	@Test
	public void testTinyIntUnsigned() throws Exception {
		executeSql("CREATE TABLE " + table + "(id TINYINT UNSIGNED)");
		executeSql("INSERT INTO " + table + " values(0)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id= 255 WHERE id=0");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Integer);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Integer(255)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new Integer(0)));

			}
		});
	}

	@Test
	public void testNumeric() throws Exception {
		executeSql("CREATE TABLE " + table + "(id NUMERIC(4,2))");
		executeSql("INSERT INTO " + table + " values(-99.99)");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id=0 WHERE id=-99.99");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof BigDecimal);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new BigDecimal("-99.99")));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new BigDecimal("0.00")));

			}
		});
	}

	@Test
	public void testSet() throws Exception {
		executeSql("CREATE TABLE " + table + "(id SET('one','two','three','four'))");
		executeSql("INSERT INTO " + table + " values('four')");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id='two' WHERE id='four'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Long);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new Long(8)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Long(2)));

			}
		});
	}

	@Test
	public void testTime() throws Exception {
		executeSql("CREATE TABLE " + table + "(id TIME)");
		executeSql("INSERT INTO " + table + " values('22:22:22')");
		waitForSync(50);
		test(new TestLogic() {

			@SuppressWarnings("deprecation")
			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id='11:11:11' WHERE id='22:22:22'");
				waitForSync(50);
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Time);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue().equals(new Time(22, 22, 22)));
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getNewValue().equals(new Time(11, 11, 11)));

			}
		});
	}

	@Test
	public void testYear() throws Exception {
		executeSql("CREATE TABLE " + table + "(id YEAR(4))");
		executeSql("INSERT INTO " + table + " values('1901')");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id='2155' WHERE id='1901'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Short);
				Assert.assertEquals((short) 2155, rowChangedEvent.getColumns().get("id").getNewValue());
				Assert.assertEquals((short) 1901, rowChangedEvent.getColumns().get("id").getOldValue());

			}
		});
	}

	@Test
	public void testTimeStamp() throws Exception {
		executeSql("CREATE TABLE " + table + "(id TIMESTAMP(4))");
		executeSql("INSERT INTO " + table + " values('2008-08-08 14:47:11')");
		waitForSync(50);
		test(new TestLogic() {

			@SuppressWarnings("deprecation")
			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table + " SET id='2022-02-02 14:22:11' WHERE id='2008-08-08 14:47:11'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof Timestamp);
				Assert.assertEquals(new Timestamp(122, 1, 2, 14, 22, 11, 0), rowChangedEvent.getColumns().get("id")
						.getNewValue());
				Assert.assertEquals(new Timestamp(108, 7, 8, 14, 47, 11, 0), rowChangedEvent.getColumns().get("id")
						.getOldValue());

			}
		});
	}

	@Test
	public void testVarchar() throws Exception {
		executeSql("CREATE TABLE " + table + "(id VARCHAR(50))");
		executeSql("INSERT INTO " + table + " values('Early birds catch the bug.')");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table
						+ " SET id='Early bug is eaten by the bird.' WHERE id='Early birds catch the bug.'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof String);
				Assert.assertEquals(new String("Early birds catch the bug."), rowChangedEvent.getColumns().get("id")
						.getOldValue());
				Assert.assertEquals(new String("Early bug is eaten by the bird."),
						rowChangedEvent.getColumns().get("id").getNewValue());

			}
		});
	}

	@Test
	public void testText() throws Exception {
		executeSql("CREATE TABLE " + table + "(id TEXT)");
		executeSql("INSERT INTO " + table + " values('Early birds catch the bug.')");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table
						+ " SET id='Early bug is eaten by the bird.' WHERE id='Early birds catch the bug.'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof byte[]);
				Assert.assertArrayEquals((new String("Early birds catch the bug.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getOldValue());
				Assert.assertArrayEquals((new String("Early bug is eaten by the bird.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getNewValue());

			}
		});
	}

	@Test
	public void testTinyText() throws Exception {
		executeSql("CREATE TABLE " + table + "(id TINYTEXT)");
		executeSql("INSERT INTO " + table + " values('Early birds catch the bug.')");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table
						+ " SET id='Early bug is eaten by the bird.' WHERE id='Early birds catch the bug.'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof byte[]);
				Assert.assertArrayEquals((new String("Early birds catch the bug.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getOldValue());
				Assert.assertArrayEquals((new String("Early bug is eaten by the bird.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getNewValue());

			}
		});
	}

	@Test
	public void testMediumText() throws Exception {
		executeSql("CREATE TABLE " + table + "(id MEDIUMTEXT)");
		executeSql("INSERT INTO " + table + " values('Early birds catch the bug.')");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table
						+ " SET id='Early bug is eaten by the bird.' WHERE id='Early birds catch the bug.'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof byte[]);
				Assert.assertArrayEquals((new String("Early birds catch the bug.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getOldValue());
				Assert.assertArrayEquals((new String("Early bug is eaten by the bird.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getNewValue());

			}
		});
	}

	@Test
	public void testLongText() throws Exception {
		executeSql("CREATE TABLE " + table + "(id LONGTEXT)");
		executeSql("INSERT INTO " + table + " values('Early birds catch the bug.')");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table
						+ " SET id='Early bug is eaten by the bird.' WHERE id='Early birds catch the bug.'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof byte[]);
				Assert.assertArrayEquals((new String("Early birds catch the bug.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getOldValue());
				Assert.assertArrayEquals((new String("Early bug is eaten by the bird.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getNewValue());

			}
		});
	}

	@Test
	public void testBlob() throws Exception {
		executeSql("CREATE TABLE " + table + "(id BLOB)");
		executeSql("INSERT INTO " + table + " values('Early birds catch the bug.')");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table
						+ " SET id='Early bug is eaten by the bird.' WHERE id='Early birds catch the bug.'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof byte[]);
				Assert.assertArrayEquals((new String("Early birds catch the bug.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getOldValue());
				Assert.assertArrayEquals((new String("Early bug is eaten by the bird.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getNewValue());

			}
		});
	}

	@Test
	public void testTinyBlob() throws Exception {
		executeSql("CREATE TABLE " + table + "(id TINYBLOB)");
		executeSql("INSERT INTO " + table + " values('Early birds catch the bug.')");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table
						+ " SET id='Early bug is eaten by the bird.' WHERE id='Early birds catch the bug.'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof byte[]);
				Assert.assertArrayEquals((new String("Early birds catch the bug.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getOldValue());
				Assert.assertArrayEquals((new String("Early bug is eaten by the bird.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getNewValue());

			}
		});
	}

	@Test
	public void testMediumBlob() throws Exception {
		executeSql("CREATE TABLE " + table + "(id MEDIUMBLOB)");
		executeSql("INSERT INTO " + table + " values('Early birds catch the bug.')");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table
						+ " SET id='Early bug is eaten by the bird.' WHERE id='Early birds catch the bug.'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof byte[]);
				Assert.assertArrayEquals((new String("Early birds catch the bug.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getOldValue());
				Assert.assertArrayEquals((new String("Early bug is eaten by the bird.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getNewValue());

			}
		});
	}

	@Test
	public void testLongBlob() throws Exception {
		executeSql("CREATE TABLE " + table + "(id LONGBLOB)");
		executeSql("INSERT INTO " + table + " values('Early birds catch the bug.')");
		waitForSync(50);
		test(new TestLogic() {

			@Override
			public void doLogic() throws Exception {
				executeSql("UPDATE " + table
						+ " SET id='Early bug is eaten by the bird.' WHERE id='Early birds catch the bug.'");
				List<ChangedEvent> events = getEvents(1, false);
				RowChangedEvent rowChangedEvent = (RowChangedEvent) events.get(0);
				Assert.assertTrue(rowChangedEvent.getColumns().get("id").getOldValue() instanceof byte[]);
				Assert.assertArrayEquals((new String("Early birds catch the bug.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getOldValue());
				Assert.assertArrayEquals((new String("Early bug is eaten by the bird.")).getBytes(),
						(byte[]) rowChangedEvent.getColumns().get("id").getNewValue());

			}
		});
	}

	public void doAfter() throws Exception {
		executeSql("DROP TABLE " + table);
	}

}
