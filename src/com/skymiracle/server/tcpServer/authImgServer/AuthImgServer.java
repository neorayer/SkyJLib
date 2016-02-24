package com.skymiracle.server.tcpServer.authImgServer;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;

import com.skymiracle.image.SkyImage;
import com.skymiracle.image.SkyImageImpl;
import com.skymiracle.server.ServerInfo;
import com.skymiracle.server.tcpServer.TcpServer;
import com.skymiracle.util.UUID;

public class AuthImgServer extends TcpServer {

	private String imgDirPath = "/tmp/authImg/";

	private static int DEFULT_PORT = 9999;

	public AuthImgServer() throws Exception {
		super("AUTHIMG", DEFULT_PORT, AuthImgServerConnHandler.class);
		File imgDirPathFile = new File(this.imgDirPath);
		if (!imgDirPathFile.exists()) {
			info("AuthImgServer.imgDirPath does not exists. Create it force.");
			imgDirPathFile.mkdirs();
		}
		this.defaultCharset = "ASCII";
	}

	public String createAuthImage(String v) throws IOException {
		SkyImage si = new SkyImageImpl(SkyImage.FORMAT_PNG);
		si.createAuthImage(v, new Font("Arial", Font.BOLD, 24), Color.WHITE,
				Color.BLUE, 0.5);
		String uuid = new UUID().toShortString();
		String imgPath = this.imgDirPath + "/" + uuid + ".png";
		si.saveAs(imgPath, SkyImage.FORMAT_PNG);
		return imgPath;
	}

	@Override
	protected ServerInfo newServerInfoInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String args[]) throws Exception {
		AuthImgServer authImgServer = new AuthImgServer();
		authImgServer.start();
	}
}
