package com.skymiracle.sor.exception;

import java.io.IOException;

import javax.servlet.ServletException;

import com.skymiracle.sor.SorRequest;
import com.skymiracle.sor.SorResponse;

public class ControllerInvokeException extends SorException {

	private static final long serialVersionUID = 1L;

	String errName;
	
	String help;

	public ControllerInvokeException(String methodName, Exception e) {
		errName = "Web控制器调用错误！";
		help = "1 检查URL; 2检查控制器类方法名；3 检查方法是否符合vi_resource_operate格式。";
		help += "推荐代码：";
		help += "<textarea>";
		help += "public void " + methodName + "(ActResult actResult) throws  AppException,  Exception {\r\n";
		help += "}";
		help += "</textarea>";
	}

	@Override
	public String getReason() {
		return errName;
	}

	@Override
	public void outToResponse(SorRequest request, SorResponse response)
			throws IOException, ServletException {
		response.getWriter().write(errName + help);
	}

}
