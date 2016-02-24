package com.skymiracle.fileBox;

import java.util.ArrayList;
import java.util.List;

import com.skymiracle.mime.MimeHeader;
import com.skymiracle.mime.MimeHeaderImpl;

public class MailBoxLsReaderImpl {

	private MailFileBox mailBox;

	private List<FileInfo> fileInfos;

	public MailBoxLsReaderImpl(MailFileBox mailBox, String pathInBox)
			throws Exception {
		this.mailBox = mailBox;
		this.fileInfos = this.mailBox.lsFileInfos(pathInBox);
	}

	public ArrayList<MailBoxLsItem> lsMail() throws Exception {
		ArrayList<MailBoxLsItem> mailInfoList = new ArrayList<MailBoxLsItem> ();
		for (FileInfo fileInfo: this.fileInfos) {
			if (fileInfo.isDirectory())
				continue;
			String fileName = fileInfo.getName();
			MimeHeader mimeHeader = new MimeHeaderImpl(fileInfo
					.getAbsolutePath());
			MailBoxLsItem mailInfo = new MailBoxLsItem(fileName, mimeHeader,
					fileInfo);
			mailInfoList.add(mailInfo);
		}
		return mailInfoList;
	}

}
