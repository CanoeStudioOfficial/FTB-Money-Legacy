package com.feed_the_beast.mods.money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

/**
 * 浮点数金钱处理工具类
 * 提供精确的浮点数运算、验证和类型转换功能
 * 确保跨平台兼容性和精度控制
 * 
 * @author FTB Money Team
 */
public final class FloatMoneyHelper
{
	public static final int DEFAULT_SCALE = 2;
	public static final double MIN_VALUE = 0.0;
	public static final double MAX_VALUE = (double) Long.MAX_VALUE / Math.pow(10, DEFAULT_SCALE);
	
	private static final BigDecimal MULTIPLIER = BigDecimal.valueOf(Math.pow(10, DEFAULT_SCALE));
	private static final DecimalFormat DECIMAL_FORMAT;
	
	static
	{
		DECIMAL_FORMAT = new DecimalFormat("#0.##");
		DECIMAL_FORMAT.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
		DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_UP);
	}
	
	private FloatMoneyHelper()
	{
	}

	/**
	 * 验证浮点数金钱值是否有效
	 * 
	 * @param value 要验证的值
	 * @return 如果值有效返回true，否则返回false
	 */
	public static boolean isValid(double value)
	{
		return !Double.isNaN(value) && !Double.isInfinite(value) && value >= MIN_VALUE && value <= MAX_VALUE;
	}

	/**
	 * 验证并规范化浮点数金钱值
	 * 无效值将被转换为0
	 * 
	 * @param value 要验证的值
	 * @return 规范化后的有效值
	 */
	public static double normalize(double value)
	{
		if (!isValid(value))
		{
			return 0.0;
		}
		return roundToScale(value);
	}

	/**
	 * 将浮点数金钱值四舍五入到指定精度
	 * 
	 * @param value 要处理的值
	 * @return 四舍五入后的值
	 */
	public static double roundToScale(double value)
	{
		return roundToScale(value, DEFAULT_SCALE);
	}

	/**
	 * 将浮点数金钱值四舍五入到指定精度
	 * 
	 * @param value 要处理的值
	 * @param scale 小数位数
	 * @return 四舍五入后的值
	 */
	public static double roundToScale(double value, int scale)
	{
		if (Double.isNaN(value) || Double.isInfinite(value))
		{
			return 0.0;
		}
		
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(scale, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * 将浮点数金钱转换为内部存储用的long值
	 * 使用定点数表示法，精度由DEFAULT_SCALE决定
	 * 
	 * @param value 浮点数金钱值
	 * @return 内部存储用的long值
	 */
	public static long toInternal(double value)
	{
		if (!isValid(value))
		{
			return 0L;
		}
		
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
		bd = bd.multiply(MULTIPLIER);
		return bd.longValue();
	}

	/**
	 * 将内部存储的long值转换为浮点数金钱
	 * 
	 * @param value 内部存储的long值
	 * @return 浮点数金钱值
	 */
	public static double fromInternal(long value)
	{
		if (value <= 0L)
		{
			return 0.0;
		}
		
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.divide(MULTIPLIER, DEFAULT_SCALE, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * 安全解析字符串为浮点数金钱
	 * 支持多种格式的输入
	 * 
	 * @param str 要解析的字符串
	 * @return 解析后的浮点数金钱值，解析失败返回0
	 */
	public static double parse(String str)
	{
		return parse(str, 0.0);
	}

	/**
	 * 安全解析字符串为浮点数金钱
	 * 支持多种格式的输入
	 * 
	 * @param str 要解析的字符串
	 * @param defaultValue 解析失败时的默认值
	 * @return 解析后的浮点数金钱值
	 */
	public static double parse(String str, double defaultValue)
	{
		if (str == null || str.trim().isEmpty())
		{
			return defaultValue;
		}
		
		String trimmed = str.trim();
		
		if (trimmed.startsWith("~"))
		{
			trimmed = trimmed.substring(1);
		}
		
		trimmed = trimmed.replace(",", ".");
		trimmed = trimmed.replaceAll("[^0-9.\\-+]", "");
		
		if (trimmed.isEmpty() || trimmed.equals(".") || trimmed.equals("-") || trimmed.equals("+"))
		{
			return defaultValue;
		}
		
		try
		{
			double value = Double.parseDouble(trimmed);
			return normalize(value);
		}
		catch (NumberFormatException e)
		{
			try
			{
				Number number = DECIMAL_FORMAT.parse(trimmed);
				return normalize(number.doubleValue());
			}
			catch (ParseException ex)
			{
				return defaultValue;
			}
		}
	}

	/**
	 * 格式化浮点数金钱为显示字符串
	 * 
	 * @param value 浮点数金钱值
	 * @return 格式化后的字符串
	 */
	public static String format(double value)
	{
		return format(value, true);
	}

	/**
	 * 格式化浮点数金钱为显示字符串
	 * 
	 * @param value 浮点数金钱值
	 * @param withSymbol 是否包含货币符号
	 * @return 格式化后的字符串
	 */
	public static String format(double value, boolean withSymbol)
	{
		if (!isValid(value))
		{
			return withSymbol ? "\u0398 0.00" : "0.00";
		}
		
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
		
		String formatted;
		if (bd.scale() == 0)
		{
			formatted = String.format(Locale.US, "%,d", bd.longValue());
		}
		else
		{
			formatted = String.format(Locale.US, "%,.2f", bd.doubleValue());
		}
		
		return withSymbol ? "\u0398 " + formatted : formatted;
	}

	/**
	 * 格式化浮点数金钱为带符号的显示字符串（带+/-前缀）
	 * 
	 * @param value 浮点数金钱值（可正可负）
	 * @return 格式化后的字符串
	 */
	public static String formatWithSign(double value)
	{
		if (value == 0.0)
		{
			return "\u0398 0.00";
		}
		
		String sign = value > 0 ? "+" : "-";
		return "\u0398 " + sign + format(Math.abs(value), false);
	}

	/**
	 * 精确加法运算
	 * 
	 * @param a 第一个操作数
	 * @param b 第二个操作数
	 * @return 精确的和
	 */
	public static double add(double a, double b)
	{
		BigDecimal bdA = BigDecimal.valueOf(normalize(a));
		BigDecimal bdB = BigDecimal.valueOf(normalize(b));
		return bdA.add(bdB).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 精确减法运算
	 * 
	 * @param a 被减数
	 * @param b 减数
	 * @return 精确的差
	 */
	public static double subtract(double a, double b)
	{
		BigDecimal bdA = BigDecimal.valueOf(normalize(a));
		BigDecimal bdB = BigDecimal.valueOf(normalize(b));
		return bdA.subtract(bdB).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 精确乘法运算
	 * 
	 * @param a 第一个操作数
	 * @param b 第二个操作数
	 * @return 精确的积
	 */
	public static double multiply(double a, double b)
	{
		BigDecimal bdA = BigDecimal.valueOf(normalize(a));
		BigDecimal bdB = BigDecimal.valueOf(normalize(b));
		return bdA.multiply(bdB).setScale(DEFAULT_SCALE, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 精确除法运算
	 * 
	 * @param a 被除数
	 * @param b 除数
	 * @return 精确的商，除数为0时返回0
	 */
	public static double divide(double a, double b)
	{
		if (b == 0.0)
		{
			return 0.0;
		}
		
		BigDecimal bdA = BigDecimal.valueOf(normalize(a));
		BigDecimal bdB = BigDecimal.valueOf(normalize(b));
		return bdA.divide(bdB, DEFAULT_SCALE, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 比较两个浮点数金钱值
	 * 
	 * @param a 第一个值
	 * @param b 第二个值
	 * @return 如果a小于b返回-1，相等返回0，大于返回1
	 */
	public static int compare(double a, double b)
	{
		BigDecimal bdA = BigDecimal.valueOf(normalize(a));
		BigDecimal bdB = BigDecimal.valueOf(normalize(b));
		return bdA.compareTo(bdB);
	}

	/**
	 * 检查两个浮点数金钱值是否相等
	 * 
	 * @param a 第一个值
	 * @param b 第二个值
	 * @return 如果相等返回true
	 */
	public static boolean equals(double a, double b)
	{
		return compare(a, b) == 0;
	}

	/**
	 * 检查浮点数金钱值是否为零
	 * 
	 * @param value 要检查的值
	 * @return 如果值为零返回true
	 */
	public static boolean isZero(double value)
	{
		return compare(value, 0.0) == 0;
	}

	/**
	 * 检查浮点数金钱值是否为正数
	 * 
	 * @param value 要检查的值
	 * @return 如果值为正数返回true
	 */
	public static boolean isPositive(double value)
	{
		return compare(value, 0.0) > 0;
	}

	/**
	 * 检查浮点数金钱值是否为负数
	 * 
	 * @param value 要检查的值
	 * @return 如果值为负数返回true
	 */
	public static boolean isNegative(double value)
	{
		return compare(value, 0.0) < 0;
	}

	/**
	 * 获取两个值中的较大值
	 * 
	 * @param a 第一个值
	 * @param b 第二个值
	 * @return 较大值
	 */
	public static double max(double a, double b)
	{
		return compare(a, b) >= 0 ? a : b;
	}

	/**
	 * 获取两个值中的较小值
	 * 
	 * @param a 第一个值
	 * @param b 第二个值
	 * @return 较小值
	 */
	public static double min(double a, double b)
	{
		return compare(a, b) <= 0 ? a : b;
	}

	/**
	 * 将值限制在指定范围内
	 * 
	 * @param value 要限制的值
	 * @param min 最小值
	 * @param max 最大值
	 * @return 限制后的值
	 */
	public static double clamp(double value, double min, double max)
	{
		value = normalize(value);
		min = normalize(min);
		max = normalize(max);
		
		if (compare(value, min) < 0)
		{
			return min;
		}
		if (compare(value, max) > 0)
		{
			return max;
		}
		return value;
	}
}
