$(document).ready(function() {
//	execute("WAKE");
	startupTimer(1000);
	$("a[rel*=facebox]").facebox();
	$("#autotestBtn").toggle(
		function() {
			$("#autotestDiv").show("slow");
			loadTestcases();
			var first_select = $("#autotestDiv form select:first");
			var second_select = $("#autotestDiv form select:last");
			var s = "<option value=''>请选择</option>";
			for(var i = 0; i < g_testcases.length; i++) {
				s += "<option value='" + g_testcases[i].title + "'>" + g_testcases[i].title + "</option>";
			}
			first_select.html(s).bind("change", function() {
				var first_project = first_select.val();
				var ss = "<option value=''>请选择</option>";
				for(var i = 0; i < g_testcases.length; i++) {
					if(g_testcases[i].title == first_project) {
						var testcases = g_testcases[i].children;
						for(var j = 0; j < testcases.length; j++) {
							ss += "<option value='" + testcases[j].title + "'>" + testcases[j].title + "</option>";
						}
					}
				}
				second_select.html(ss);
			})
		}, function() {
			closeDiv("autotest");
		}
	);
	loadTestcases();
	var operationTipsContent = 
		'<img src="../image/a.gif"/>&nbsp;点击屏幕/键盘:鼠标左键点击</br>'
			+ '<img src="../image/a.gif"/>&nbsp;长按屏幕/键盘:鼠标左键按下持续1秒后松开</br>'
			+ '<img src="../image/a.gif"/>&nbsp;滑动屏幕:鼠标左键在屏幕上按下不松开，沿滑屏方向滑动鼠标后，松开左键即可';
	$("#operationTips").hovercard({
		detailsHTML: operationTipsContent,
		width: 400
	});
	$("#screenQSlider").slider({
		value : 5,
		min : 1,
		max : 10,
		step : 1,
		slide : function(event, ui) {
			var tips;
			switch(ui.value) {
				case 1:tips = "速度优先";break;
				case 2:tips = "最佳---";break;
				case 3:tips = "最佳--";break;
				case 4:tips = "最佳-";break;
				case 5:tips = "最佳";break;
				case 6:tips = "最佳+";break;
				case 7:tips = "最佳++";break;
				case 8:tips = "最佳+++";break;
				case 9:tips = "最佳++++";break;
				case 10:tips = "质量优先";break;
			}
			$("#screenQAmount").html(tips);
			g_screenQ = ui.value;
		}
	});
});
var g_username;
var g_fromUrl;
var g_server;
var g_serialNumber;
var g_screenQ = 5;

function startupCountdownTimer(username, fromUrl, server, serialNumber, lefttime, interval, period) {
	g_username = username;
	g_fromUrl = fromUrl;
	g_server = server;
	g_serialNumber = serialNumber;
	var c = period;
	window.setInterval(function() {
		lefttime--;
		dispCountdownTimer(lefttime - 0);
		if(lefttime - 0 == 0) {
			updateLefttime(0);
			logout();
		}
		if(--c == 0) {
			updateLefttime(lefttime);
			c = period;
		}
	}, interval * 1000);
}
function dispCountdownTimer(lefttime) {
	var h = Math.floor(lefttime / 3600);
	var temp = lefttime - h * 3600;
	var m = Math.floor(temp / 60);
	var s = temp - m * 60;
	if(m < 10) m = '0' + m;
	if(s < 10) s = '0' + s;
	$("#hour").html(h);
	$("#minute").html(m);
	$("#second").html(s);
}

function updateLefttime(lefttime) {
	$.ajax({
		url: "../servlet/UpdateLefttime1",
		data: "lefttime=" + lefttime,
		success: function(data) {
			switch(data) {
				case 0: case "0": // do nothing
					break;
				case 1: case "1": // update lefttime
					break;
				case 2: case "2": // logout
					logout();
					break;
				case 3: case "3": // error
					break;
			}
		}
	});
}

//function parseUrl1(url) {
//	var parts = url.split('/');
//	return parts[0] + "//" + parts[2] + "/" + parts[3] + "/";
////	return "http://localhost:8080/User/";
//}
var timer;
function startupTimer(interval) {
	shutdownTimer();
	timer = window.setTimeout(function() {
//		execute("AUTO");
		snapshot();
	}, interval);
}
function shutdownTimer() {
	if(timer) window.clearTimeout(timer);
}

var pos1, pos2, time1, time2;
var currentScreen;

function start(e) {
	pos1 = getXY(e); time1 = new Date().getTime();
}
function finish(e) {
	pos2 = getXY(e); time2 = new Date().getTime();
	var cmd;
	if(isNearby(pos1, pos2)) {
		cmd = "TOUCH " + pos1.x + " " + pos1.y;
		if(isLongPress(time1, time2)) {
			//long press
			cmd += " DOWN";
		} else {
			//short press
			cmd += " DOWN_AND_UP";
		}
	} else {
		//move
		var steps = 10, ms = 1000;
		cmd = "DRAG " + pos1.x + " " + pos1.y + " " + pos2.x + " " + pos2.y + " " + steps + " " + ms;
	}
	execute(cmd);
}
function isNearby(p1, p2) {
	if(Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2)) > 10) return false;
	return true;
}
function isLongPress(t1, t2) {
	if((t2 - t1) > 800) return true;
	return false;
}

//reboot
function reboot(into) {
	//reboot into??
}
function realPosition(x) {
	return Math.floor(x * (g_width / 330));
}
//press operation
function pressDown() { //time = 0; 
	time1 = new Date().getTime(); }
function pressUp(t) {
	time2 = new Date().getTime();
	if(isLongPress(time1, time2)) execute("PRESS " + t.id.toUpperCase() + " DOWN");
	else execute("PRESS " + t.id.toUpperCase() + " DOWN_AND_UP");
}
function press(keyName) {
	execute("PRESS " + keyName);
}
function typeAction() {
	var typeContent = $("#t").val();
	if(!typeContent) return;
	$("#t").val("");
	var cmd = "TYPE " + typeContent;
	execute(cmd);
}
//close operation
function closeDiv(op) {
	switch(op) {
		case "upfile":
			$("#upfileDiv").hide("slow");
			break;
		case "autotest":
			$("#autotestDiv").hide("slow");
			break;
		default:
			break;
	};
}

var proTimer;
function uploadfile() {
	$("#facebox div[id='upfileTips']").html("");
	var file = $("#facebox input[name='upfile']").val();
	if(!file) {
		$("#facebox div[id='upfileTips']").html("<strong> 请选择文件 </strong>");
		return;
	}
	if(hasChinese(file)) {
		$("#facebox div[id='upfileTips']").html("<strong> 无法上传带有中文字符的文件，请修改后重试 </strong>");
		return;
	}
	$("#facebox div[id='upfileTips']").html("<strong> 正在上传... </strong>");
	var url = "../servlet/Upfiles?way=upload";
	$("form[name='upfileForm']").attr("action", url).submit();
	$("#facebox input:button").attr("disabled", "disabled");
	
	proTimer = window.setInterval(function() {
		$.ajax({
			url: "../servlet/UpProgress?roll="+Math.random(),
			success: function(data) {
				if(data == "100.0" || data == "101.0" || data == "101") {
					window.clearInterval(proTimer);
					$("#facebox div[id='upfileTips']").html("<strong> 上传成功，2秒钟后自动关闭. </strong>");
					window.setTimeout(function() {
						jQuery(document).trigger('close.facebox');
					}, 1600);
				} else {
					$("#facebox div[id='upfileTips']").html("已经上传<strong> "+data+"%</strong>");
				}
			}
		});
	}, 800);
	
	execute("UPLOAD " + file);
}
var apkFormCount = 1;
function uploadResult(data) {
	// insert the record into 'Wo De Ying Yong'
	var new_apk = 
		" <div class='bg-grey003 itemsBg'>"
		+ "	<p class='news'>"
		+ "		<img src='../image/a.gif'/> " + data + "<br/>"
		+ "	</p>"
		+ "	<span>"
		+ "		<form name='apkForm" + apkFormCount + "' method='POST' target='hiddenApkFrame'>"
		+ "			<table>"
		+ "				<tr>"
		+ "					<td class='sync'><a onClick=\"appManager(1," + apkFormCount + ",'" + data + "')\">同步</a></td>"
		;
	if(isApkFile(data)) {
		new_apk +=
			"				<td class='install'><a onClick=\"appManager(2," + apkFormCount + ",'" + data + "')\">安装</a></td>"
			+ " 			<td class='reinstall'><a onClick=\"appManager(3," + apkFormCount + ",'" + data + "')\">覆盖安装</a></td>"
			;
	} 
	new_apk +=
		"				</tr>"
		+ "			</table>"
		+ "			<input type='hidden' name='apkName' value='" + data + "'/>"
		+ "		</form>"
		+ "	</span>"
		+"</div>"
		;
	apkFormCount++;
	$("#apks").append(new_apk);
}
function appManager(type, count, file) {
	var url, tips, history;
	switch(type) {
		case 1: // Sync
			url = "../servlet/ExecuteCommands?req=SYNC " + file;
			tips = "正在同步...";
			history = "SYNC ";
			break;
		case 2: // Install
			if(!isApkFile(file)) {
				$("#apkTips").html("<strong>文件格式错误！</strong>");
				return;
			}
			url = "../servlet/ExecuteCommands?req=INSTALL " + file;
			tips = "正在安装...";
			history = "INSTALL ";
			break;
		case 3: // Reinstall
			if(!isApkFile(file)) {
				$("#apkTips").html("<strong>文件格式错误！</strong>");
				return;
			}
			url = "../servlet/ExecuteCommands?req=REINSTALL " + file;
			tips = "正在安装...";
			history = "REINSTALL ";
			break;
	}
	$("#apkTips").html("<img src='../image/loading.gif' style='height: 12px;'/>&nbsp;" + tips);
	$.ajax({
		url: url,
		dataType: "json",
		success: function(data) {
			if(data.length != 0) {
				switch(data.result) {
					case "SYNC_SUCCESS":
						$("#apkTips").html("<img src='../image/state_success.jpg' style='height: 12px;'/>&nbsp;同步成功");
						var sync = $("form[name='apkForm" + count + "']").find("td.sync");
						if(sync.next(".remove").length <= 0) sync.after("<td class='remove'><a onClick=\"remove(this, '" + data.file + "')\">删除</a></td>");
						break;
					case "INSTALL_SUCCESS":
						$("#apkTips").html("<img src='../image/state_success.jpg' style='height: 12px;'/>&nbsp;安装成功");
						var reinstall = $("form[name='apkForm" + count + "']").find("td.reinstall");
						if(reinstall.next(".uninstall").length <= 0 && reinstall.next(".start").length <= 0) {
							reinstall.after("<td class='start'><a onClick=\"startActivity('" + data.component + "')\">启动应用</a></td>")
								.after("<td class='uninstall'><a onClick=\"uninstall(this, '" + data.packageName + "')\">卸载</a></td>");
						}
						break;
					case "SyncException": case "AdbCommandRejectedException":
						$("#apkTips").html("<img src='../image/state_failure.jpg' style='height: 12px;'/>&nbsp;同步失败:服务器异常");
						break;
					case "TimeoutException":
						$("#apkTips").html("<img src='../image/state_failure.jpg' style='height: 12px;'/>&nbsp;同步失败:系统超时");
						break;
					case "INSTALL_FAILED_ALREADY_EXISTS":
						$("#apkTips").html("<img src='../image/state_failure.jpg' style='height: 12px;'/>&nbsp;安装失败：应用程序已经存在");
						break;
					case "INSTALL_FAILED_MISSING_SHARED_LIBRARY":
						$("#apkTips").html("<img src='../image/state_failure.jpg' style='height: 12px;'/>&nbsp;安装失败：缺少共享链接库");
						break;
					case "INSTALL_FAILED_INSUFFICIENT_STORAGE":
						$("#apkTips").html("<img src='../image/state_failure.jpg' style='height: 12px;'/>&nbsp;安装失败：目标目录空间不足");
						break;
					case "INSTALL_FAILED_OLDER_SDK":
						$("#apkTips").html("<img src='../image/state_failure.jpg' style='height: 12px;'/>&nbsp;安装失败：SDK版本过低");
						break;
					default:
						tips = data.result;
						break;
				}
			}
		}
	});
	// insert the record into 'Li Shi Ji Lu'
	history += file;
//	execute(history);
	addHistory(history);
}
function isApkFile(file) {
	return /\.apk$/.test(file);
}
function hasChinese(file) {
	return /.*[\u4e00-\u9fa5]+.*$/.test(file);
}
function remove(obj, file) {
	$("#apkTips").html("<img src='../image/loading.gif' style='height: 12px;'/>&nbsp;正在删除...");
	execute("REMOVE " + file);
	$(obj).parent().remove();
	$("#apkTips").html("<img src='../image/state_success.jpg' style='height: 12px;'/>&nbsp;删除成功");
}
function uninstall(obj, packageName) {
	$("#apkTips").html("<img src='../image/loading.gif' style='height: 12px;'/>&nbsp;正在卸载...");
	execute("UNINSTALL " + packageName);
	var uninstall = $(obj).parent();
	uninstall.siblings(".start").remove();
	uninstall.remove();
	$("#apkTips").html("<img src='../image/state_success.jpg' style='height: 12px;'/>&nbsp;卸载成功");
}
function startActivity(component) {
	execute("START " + component);
	$("#apkTips").html("<img src='../image/state_success.jpg' style='height: 12px;'/>&nbsp;启动成功");
}

var g_p_length;
var g_parameters;
var g_testcases;
function loadTestcases() {
	$.ajax({
		url: "../servlet/testcases/LoadTestcases",
//		data: "username=" + g_username,
		success: function(data) {
			if(data != "null") {
				data = eval("(" + data + ")");
				g_testcases = data;
			}
		}
	});
}
function startRecordTestcase(obj) {
	$(obj).attr("disabled", "disabled").next().removeAttr("disabled");
	$("#historys").html("");
	addHistory("WAKE");
}
function finishRecordTestcase(obj) {
	$(obj).attr("disabled", "disabled").prev().removeAttr("disabled");
	var length = 0;
	var parameters = "";
	$("#historys p").each(function(i) {
		var children = $(this).children("span");
		parameters = "&command" + length + "=" + $(children[0]).html() + "&date" + length + "=" + $(children[1]).html() + parameters;
		length++;
	});
	g_p_length = length;
	g_parameters = parameters;
	jQuery.facebox({div:'#saveRecordTestcaseDiv'});
	// 濉厖娴嬭瘯椤圭洰鍒楄〃
	loadTestcases();
	var select = "<option value=''>请选择</option>";
	for(var i = 0; i < g_testcases.length; i++) {
		select += "<option value='" + g_testcases[i].title + "'>" + g_testcases[i].title + "</option>";
	}
	$("#facebox form[name='saveRecordTestcaseForm'] select").html(select);
}
function saveRecordTestcase(serialNumber) {
	var testcase = $("#facebox input[name='testcase']").val();
	var project = $("#facebox select").val();
	var tips = $("#facebox div[id='saveRecordTestcaseTips'] p").last();
	if(!project) {
		tips.addClass("red").html("请选择测试项目！");
		return;
	}
	tips.html("正在保存，请稍等片刻...");
	$.ajax({
		url: "../servlet/testcases/NewTestcase",
		data: "serialNumber=" + serialNumber + "&username=" + username + "&project=" + project 
			+ "&testcase=" + testcase + "&length=" + g_p_length + g_parameters,
		dataType: "POST",
		success: function(data) {
			switch(data) {
				case 0:
					tips.addClass("green").html("保存成功！2秒钟后自动关闭！");
					loadTestcases();
					break;
				case 2:
					tips.addClass("red").html("服务器异常，请联系我们！");
					break;
			}
			window.setTimeout(function() {
				jQuery(document).trigger('close.facebox');
			}, 1600);
		}
	});
}
function runTestcase(serialNumber) {
	var project = $("#autotestDiv form select:first").val();
	var testcase = $("#autotestDiv form select:last").val();
	if(!project || !testcase) {
		$("#autotestTips").addClass("tips red").html("请选择测试案例！");
		return;
	}
	$("#autotestTips").addClass("tips").html("运行中，请稍等片刻...");
	$.ajax({
		url: "../servlet/testcases/RunTestcase",
		data: "username=" + g_username + "&project=" + project + "&name=" + testcase + "&serialNumber=" + serialNumber,
		success: function(data) {
			if(data == "OK") {
				$("#autotestTips").addClass("green").html("成功，请联系我们获取结果！");
			} else {
				$("#autotestTips").addClass("red").html("服务器异常，请联系我们！");
			}
		}
	});
}

function dispProBar(percentage, status) {
	switch(status) {
		case "success":
			$("#progressBar").progressBar(percentage, {barImage: '../image/progressBar/progressbg_green.gif'});
			break;
		case "exists":
			$("#progressBar").progressBar(percentage, {barImage: '../image/progressBar/progressbg_red.gif'});
			break;
		case "library":
			$("#progressBar").progressBar(percentage, {barImage: '../image/progressBar/progressbg_orange.gif'});
			break;
		case "undefine":
			$("#progressBar").progressBar(percentage, {barImage: '../image/progressBar/progressbg_red.gif'});
			break;
		default:
			$("#progressBar").progressBar();
			break;
	}
}

var count = 0;
function execute(cmd) {
	addHistory(cmd);
	$.ajax({
		url: "../servlet/ExecuteCommands",
		data: "req=" + cmd + "&q=" + g_screenQ + "&t=" + Math.random(),
		dataType: "json",
		success: function(data) {	
			if(data.length != 0) {
				switch(data.result) {
					case "TIMEOUT":
						logout();
						break;
					case "FORBIDDEN":
						break;
				}
			}
		}
	});
}
function snapshot() {
	$.ajax({
		url: "../servlet/Snapshot",
		data: "q=" + g_screenQ + "&t=" + Math.random(),
		timeout: 5000,
		error: function() {
			if(count++ >= 10) {
				//turn to manual way to refresh
				count = 0;
				$("#automatic").removeAttr("checked");
				$("#manual").attr("checked", "checked");
			} else {
				startupTimer(2000);
			}
		},
		success: function(data) {		
			if(count-- < 0) count = 0;
			if(data == "TIMEOUT") {
				gotoDeviceList();
			} else {
				$("#screenLoading").hide();
				$("#screen").ImageSwitch({NewImage:data, EffectOriginal: false, Speed:0});
				currentScreen = $.trim(data);
			}
			startupTimer(1000);
		}
	});
}
function gotoDeviceList() {
	window.location = g_fromUrl;
}
function logout() {	
	window.location = "../servlet/Unlock";
}
function addHistory(data) {
	var his = 
		"<p class='news itemsBg'>" +
		"	<img src='../image/a.gif'/>&nbsp;" +
		"	<span style='display: none;'>" + data + "</span>" +
		"	<span style='display: none;'>" + new Date().getTime() + "</span>" +
		"	<span>" + parseOperation(data) + "</span>" +
		"</p>";
	$("#historys").prepend(his);
}

function capture() {
	$("#currentScreen img").attr("src", currentScreen);
	$("#currentScreen a").attr("href", currentScreen).html(currentScreen);
	
	jQuery.facebox({div:'#currentScreen'});
}

function getXY(e) {
	e = window.event || e;
	var x = (e.offsetX === undefined) ? getOffset(e).offsetX : e.offsetX;
	var y = (e.offsetY === undefined) ? getOffset(e).offsetY : e.offsetY;
	x = realPosition(x);
	y = realPosition(y);
	var pos = {"x":x, "y":y};
	return pos;
}
function getOffset(e) {
	var target = e.target;
	if (target.offsetLeft === undefined) {
		target = target.parentNode;
	}
	var pageCoord = getPageCoord(target);
	var eventCoord = {x:window.pageXOffset + e.clientX, y:window.pageYOffset + e.clientY};
	var offset = {offsetX:eventCoord.x - pageCoord.x, offsetY:eventCoord.y - pageCoord.y};
	return offset;
}
function getPageCoord(element) {
	var coord = {x:0, y:0};
	while (element) {
		coord.x += element.offsetLeft;
		coord.y += element.offsetTop;
		element = element.offsetParent;
	}
	return coord;
}

function parseData(data) {
	var datas = data.split(" ");
}
function parseOperation(operation) {
	if(operation == "LOGIN") {
		operation = "登陆系统";
	} else if(operation == "LOGOUT") {
		operation = "退出系统";
	} else if(operation == "LOCK") {
		operation = "锁定设备";
	} else if(operation == "UNLOCK") {
		operation = "释放设备";
	} else {
		var ops = operation.split(" ");
		switch(ops[0]) {
			case "TOUCH":
				switch(ops[3]) {
					case "DOWN":
						operation = "长按屏幕";
						break;
					case "DOWN_AND_UP":
						operation = "点击屏幕";
						break;
				}
				operation +=  "(" + ops[1] + "," + ops[2] + ")";
				break;
			case "PRESS":
				if(ops[1] == "BACK" || ops[1] == "SEARCH" || ops[1] == "UP" || ops[1] == "DOWN"
					|| ops[1] == "LEFT" || ops[1] == "RIGHT" || ops[1] == "CENTER" || ops[1] == "ENTER") {
					operation = "点击" + ops[1] + "键";
				} else {
					switch(ops[2]) {
						case "DOWN":
							operation = "长按";
							break;
						case "DOWN_AND_UP":
							operation = "点击";
							break;
					}
					operation += ops[1] + "键";
				}
				break;
			case "DRAG":
				operation = "滑动屏幕(" + ops[1] + "," + ops[2] + ")到(" + ops[3] + "," + ops[4] + ")";
				break;
			case "TYPE":
				operation = "输入" + ops[1];
				break;
			case "SYNC":
				operation = "同步" + ops[1].substring(ops[1].lastIndexOf("/") + 1, ops[1].length);
				break;
			case "REMOVE":
				operation = "删除" + ops[1];
				break;
			case "INSTALL":
				operation = "安装" + ops[1].substring(ops[1].lastIndexOf("/") + 1, ops[1].length);
				break;
			case "REINSTALL":
				operation = "覆盖安装" + ops[1].substring(ops[1].lastIndexOf("/") + 1, ops[1].length);
				break;
			case "UNINSTALL":
				operation = "卸载" + ops[1];
				break;
			case "START":
				operation = "启动" + ops[1].substring(0, ops[1].indexOf("/"));
				break;
			case "UPLOAD":
				operation = "上传" + ops[1];
				break;
		}
	}
	return operation;
}
//End