<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="java.util.Set"%>
<%@ page import="android.testing.system.util.Adb"%>
<%@ page import="android.testing.system.util.Lock"%>
<%@ page import="com.android.monkeyrunner.adb.AdbMonkeyDevice"%>
<%@ page import="com.android.ddmlib.IDevice"%>
<html>
	<head>
		<title>设备列表</title>
		<link rel="stylesheet" href="../css/style.css">
		<style type="text/css">
		.STYLE1 {
			font-size: 28px
		}
		
		.STYLE2 {
			color: #0000FF
		}
		
		TABLE,TD {
			padding: 5px;
			text-align: center;
			border: 1px solid #000;
			background-color: #EFEFEF
		}
		
		TABLE {
			width: 100%
		}
		
		A {
			cursor: pointer;
		}
		</style>
		<meta http-equiv="Refresh" content="30" />
		<!--30表示刷新频率，单位是秒-->
	</head>

	<body>
		<div class="tophead">
			<div class="logo">
				<br />
				<a>全业务能力开放新技术研究-终端测试环境系统</a>
			</div>
			<div class="banner">
				<div class="banner-line">
					<a id="home" href="http://www.xinfuonline.com" accesskey="b"
						title="首页">首页</a>
					<br>
					<font color="blue" style="font-size: 14px;"> &nbsp;</font>
				</div>
			</div>
		</div>

		<div class="newslist">
			<div class="w_tit">
				&nbsp;演示试用
			</div>
			<p style="padding-left: 10px; padding-top: 3px; margin: 5px;">
				<strong>提示:锁定后，您将有<font color="#FF0000">15分钟</font>的使用时间；超时后系统将自动释放您锁定的手机。在这期间，如果连续<font color="#FF0000">3分钟</font>你没有任何操作，该手机也将被系统自动释放。</strong>
			</p>
			<table align="center">
				<tr>
					<th>
						设备编号
					</th>
					<th>
						模拟器/手机
					</th>
					<th>
						生产商
					</th>
					<th>
						型号
					</th>
					<th>
						屏幕预览
					</th>
					<th>
						当前状态
					</th>
				</tr>
				<%
					Adb adb = Adb.getInstance();
					String sessionId = session.getId();
					int count = 0;
					IDevice[] idevices = adb.getAdbBackend().getBridge().getDevices();
					//Map<String, String> states = adb.getLock().areLocks(sessionId);
					for (IDevice idevice : idevices) {
						String serialNumber = idevice.getSerialNumber();
				%>
				<tr>
					<td width="150px"><%=serialNumber%></td>
					<td width="100px">
						<%
							boolean isEmulator = idevice.isEmulator();
								if (isEmulator)
									out.print("模拟器");
								else
									out.print("手机");
						%>
					</td>
					<td>
						<%
							if (isEmulator)
									out.print("Google Inc.");
								else
									out.print(idevice.getProperty("ro.product.manufacturer"));
						%>
					</td>
					<td>
						<%
							if (isEmulator)
									out.print("Google Inc.");
								else
									out.print(idevice.getProperty("ro.product.model"));
						%>
					</td>
					<td>
						<%
							if (isEmulator)
								out.print("<img src=\"../image/x/preview.jpg\">");
							else {
								out.print("<img src=\"../image/");
								out.print(idevice.getProperty("ro.product.manufacturer"));
								out.print("/");
								out.print(idevice.getProperty("ro.product.model"));
								out.print("/preview.jpg\">");
							}
						%>
					</td>
					<td>
						<%
								//if(!adb.getLock().isLock(serialNumber, sessionId)) {
								//	out.print("<form name='devicesForm" + count
								//			+ "' action='../servlet/ApplyLock' method='post'>");
								//	out.print("  <input type='hidden' name='serialNumber' value='"
								//					+ serialNumber + "' />");
								//	out.print("  <a onClick='javascript:document.devicesForm"
								//					+ count++ + ".submit();'><img src='../image/icon/btnLock.jpg'></a>");
								//	out.print("</form>");
								//} else {
								//	out.print("正在使用");
								//}
						%>
					</td>
				</tr>
				<%
					}
				%>
				<tr>
					<td colspan="6" style="text-align: right">
						系统自动刷新，周期
						<b>30</b> 秒
					</td>
				</tr>
			</table>
		</div>

		<hr />
		<div class="footer">
			全业务能力开放新技术研究-终端测试环境系统 版权所有&copy;2011,
			<a target="_blank" href="http://beian.vhostgo.com/">京ICP备11019760号</a>
		</div>

	</body>
</html>
