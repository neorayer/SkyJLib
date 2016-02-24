package com.skymiracle.sor.render;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianDebugInputStream;
import com.caucho.hessian.io.HessianDebugOutputStream;
import com.caucho.hessian.io.HessianOutput;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.hessian.server.HessianSkeleton;
import com.caucho.services.server.ServiceContext;
import com.skymiracle.logger.Logger;
import com.skymiracle.sor.ActResult;
import com.skymiracle.sor.exception.AppException;

public class HessianRenderer extends Renderer {

	@Override
	public void render() throws IOException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, InstantiationException, Exception {

		Class<?> _homeAPI = actResult.getHomeAPI();
		Object _homeImpl = actResult.getHomeImpl();
		HessianSkeleton _homeSkeleton = new HessianSkeleton(_homeImpl, _homeAPI);

		if (!request.getMethod().equals("POST")) {
			response.setStatus(500, "Hessian Requires POST");
			PrintWriter out = response.getWriter();

			response.setContentType("text/html");
			out.println("<h1>Hessian Requires POST</h1>");

			return;
		}

		ServiceContext.begin(request, request.getPathInfo(), null);
		try {
			InputStream is = request.getInputStream();
			OutputStream os = response.getOutputStream();
			response.setContentType("application/x-hessian");

			if (Logger.LEVEL_DEBUG <= Logger.getLevel()) {
				PrintWriter dbg = new PrintWriter(System.out);
				is = new HessianDebugInputStream(is, dbg);
				os = new HessianDebugOutputStream(os, dbg);
			}

			SerializerFactory serializerFactory = new SerializerFactory();
			// 输入流
			Hessian2Input in = new Hessian2Input(is);
			// 序列化输入流
			in.setSerializerFactory(serializerFactory);

			int code = in.read();
			if (code != 'c') {
				// XXX: deflate
				throw new IOException("expected 'c' in hessian input at "
						+ code);
			}

			// 输出流
			AbstractHessianOutput out;
			int major = in.read();
			int minor = in.read();

			if (major >= 2)
				out = new Hessian2Output(os);
			else
				out = new HessianOutput(os);

			// 序列化输出流
			out.setSerializerFactory(serializerFactory);

			_homeSkeleton.invoke(in, out);
			out.close();
		} finally {
			ServiceContext.end();
		}
	}

	@Override
	public void render(Exception e) throws IOException {
		response.getWriter().print(
				"<exception>" + e.getMessage() + "</exception>");
	}

	@Override
	public void render(AppException e) throws IOException {
		response.getWriter().print(
				"<exception>" + e.getMessage() + "</exception>");
	}

}
