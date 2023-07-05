package com.eikona.tech.service.impl;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.eikona.tech.domain.MediaSite;
import com.eikona.tech.repository.MediaSiteRepository;


@Service
@EnableScheduling
public class MultimediaLogServiceImpl {

	@Autowired
	private MediaSiteRepository mediaSiteRepository;
	
	@Autowired
	private DahuaMultimediaProcessingServiceImpl dahuaMultimediaProcessingServiceImpl;
	
	@Autowired
	private UnvMultimediaProcessingServiceImpl unvMultimediaProcessingServiceImpl;
	
	//@Scheduled(cron = "0 0/20 12,22 ? * *")
//	@Scheduled(fixedDelay = 30000)
	public void multimediaProcessingFromDirectory() {
		
		String directoryPath = "D:/workspace/UFO/Images/";

		File rootFileStr = new File("D:/workspace/UFO/Images/");
		String assetCode = null;
		for (File mediasiteFile : rootFileStr.listFiles()) {

			assetCode = mediasiteFile.getName();
			MediaSite mediasite = mediaSiteRepository.findByAssetCode(assetCode);
			String assetFolderPath = directoryPath + assetCode + "/";
			
			if(null!=mediasite.getCamera()) {
				if("Dahua".equalsIgnoreCase(mediasite.getCamera().getName())) {
					
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					String dateStr = format.format(new Date());
					
					if(mediasite.getCamera().isSaveVideo())
						dahuaMultimediaProcessingServiceImpl.videoProcessing(assetFolderPath, mediasite, dateStr);
					if(mediasite.getCamera().isSaveImage())
						dahuaMultimediaProcessingServiceImpl.imageProcessing(assetFolderPath, mediasite, dateStr);
					
				} else{
					SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
					String dateStr = format.format(new Date());
						
					if(mediasite.getCamera().isSaveImage())
						unvMultimediaProcessingServiceImpl.imageProcessing(assetFolderPath, mediasite, dateStr);
				}
			}
		}
	}
	
	public void multimediaProcessingFromDirectory(String dateStr) {
		
		String directoryPath = "D:/workspace/UFO/Images/";

		File rootFileStr = new File("D:/workspace/UFO/Images/");
		String assetCode = null;
		for (File mediasiteFile : rootFileStr.listFiles()) {

			assetCode = mediasiteFile.getName();
			MediaSite mediasite = mediaSiteRepository.findByAssetCode(assetCode);
			String assetFolderPath = directoryPath + assetCode + "/";
			
			if(null!=mediasite.getCamera()) {
				SimpleDateFormat dahuaFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat unvFormat = new SimpleDateFormat("yyyyMMdd");
				if("Dahua".equalsIgnoreCase(mediasite.getCamera().getName())) {
					
					try {
						Date date = unvFormat.parse(dateStr);
					
						String dahuaDate = dahuaFormat.format(date);
						
						if(mediasite.getCamera().isSaveVideo())
							dahuaMultimediaProcessingServiceImpl.videoProcessing(assetFolderPath, mediasite, dahuaDate);
						if(mediasite.getCamera().isSaveImage())
							dahuaMultimediaProcessingServiceImpl.imageProcessing(assetFolderPath, mediasite, dahuaDate);
					
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else{
					if(mediasite.getCamera().isSaveImage())
						unvMultimediaProcessingServiceImpl.imageProcessing(assetFolderPath, mediasite, dateStr);
				}
			}
		}
	}
}
