package nikheel.rh.fss.dao;

import java.util.List;

import nikheel.rh.fss.domain.FileMetadata;

public interface FileMetadataDAOAPI {

	void save(FileMetadata metadata);

	List<FileMetadata> findAll();

	List<FileMetadata> findByChecksum(String checksum);

	List<String> listAllFileNames();

	boolean deleteByFileName(String fileName);

	FileMetadata findByFileName(String fileName);
}
