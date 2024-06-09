package nikheel.rh.fss.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import nikheel.rh.fss.domain.FileMetadata;
import nikheel.rh.fss.domain.SortOrder;

public interface FileStoreServiceAPI {

	List<String> addFiles(MultipartFile[] files);

	boolean doesFileExist(String checksum);

	List<String> doesFilesExist(List<String> checksums);

	List<String> listFiles();

	void loadExistingFiles();

	void addFileMetadata(FileMetadata metadata);

	boolean removeFile(String fileName);

	String updateFile(MultipartFile file);

	Map<String, Integer> wordCount();

	Map<String, Integer> getFrequentWords(int limit, SortOrder order);
}
