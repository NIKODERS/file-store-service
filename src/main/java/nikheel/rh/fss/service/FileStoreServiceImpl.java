package nikheel.rh.fss.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import nikheel.rh.fss.dao.FileMetadataDAOAPI;
import nikheel.rh.fss.domain.FileMetadata;
import nikheel.rh.fss.domain.SortOrder;
import nikheel.rh.fss.exception.BadFileException;
import nikheel.rh.fss.exception.FileStorageException;
import nikheel.rh.fss.util.ChecksumCalculator;

@Service
public class FileStoreServiceImpl implements FileStoreServiceAPI {

	private static final String UPLOAD_DIR = "./uploads";
	private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("txt", "text", "log");
	@Autowired
	private final FileMetadataDAOAPI fileMetadataDAO;
	private static Logger LOGGER = LoggerFactory.getLogger(FileStoreServiceImpl.class);

	@Autowired
	public FileStoreServiceImpl(FileMetadataDAOAPI fileMetadataDAO) {
		this.fileMetadataDAO = fileMetadataDAO;
	}

	@PostConstruct
	public void init() {
		createUploadDirIfNotExist();
	}

	private void createUploadDirIfNotExist() {
		File uploadDir = new File(UPLOAD_DIR);
		if (!uploadDir.exists()) {
			boolean dirCreated = uploadDir.mkdirs();
			if (!dirCreated) {
				throw new RuntimeException("Failed to create upload directory");
			}
		}
	}

	@Override
	public List<String> addFiles(MultipartFile[] files) {
		List<String> uploadedFiles = new ArrayList<>();
		if (allFilesEmpty(files)) {
			throw new FileStorageException("No files provided for upload");
		}
		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				if (!validateFileExtension(file)) {
					throw new BadFileException("Invalid file extension. Allowed extensions are: " + ALLOWED_EXTENSIONS);
				}
			}
		}
		int duplicateCounter = 0;
		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				try {
					String fileName = file.getOriginalFilename();
					long size = file.getSize();
					LocalDateTime uploadedDate = LocalDateTime.now();
					String checksum = calculateChecksum(file.getBytes());

					if (!doesFilePreExistWithSameName(checksum, fileName)) {
						FileMetadata metadata = new FileMetadata(fileName, size, uploadedDate, checksum);
						fileMetadataDAO.save(metadata);

						Path uploadPath = Paths.get(UPLOAD_DIR, fileName);
						Files.copy(file.getInputStream(), uploadPath);

						updateCache(checksum, metadata);

						uploadedFiles.add(fileName);
					} else {
						duplicateCounter++;
					}
				} catch (IOException e) {
					throw new FileStorageException("Failed to store file " + file.getOriginalFilename(), e);
				}
			}
		}

		if (duplicateCounter == files.length && duplicateCounter != 0) {
			throw new FileStorageException("File(s) already exist with same name and content on server !!");
		}
		return uploadedFiles;
	}

	@Override
	@Cacheable(value = "fileMetadataCache", key = "#checksum")
	public boolean doesFileExist(String checksum) {
		List<FileMetadata> fileMetadataList = fileMetadataDAO.findByChecksum(checksum);
		return !ObjectUtils.isEmpty(fileMetadataList);
	}

	@Override
	public List<String> doesFilesExist(List<String> checksums) {
		List<String> existingChecksums = new ArrayList<>();
		for (String checksum : checksums) {
			if (!ObjectUtils.isEmpty(fileMetadataDAO.findByChecksum(checksum))) {
				existingChecksums.add(checksum);
			}
		}
		return existingChecksums;
	}

	@Override
	public List<String> listFiles() {
		return fileMetadataDAO.listAllFileNames();
	}

	@CachePut(value = "fileMetadataCache", key = "#checksum")
	public FileMetadata updateCache(String checksum, FileMetadata metadata) {
		return metadata;
	}

	@Override
	public void loadExistingFiles() {
		File uploadDir = new File(UPLOAD_DIR);
		if (!uploadDir.exists() || !uploadDir.isDirectory()) {
			return;
		}

		File[] files = uploadDir.listFiles();
		if (files != null) {
			for (File file : files) {
				try {
					String fileName = file.getName();
					long size = file.length();
					LocalDateTime uploadedDate = LocalDateTime.now();

					String checksum = calculateChecksum(file);

					FileMetadata metadata = new FileMetadata(fileName, size, uploadedDate, checksum);
					fileMetadataDAO.save(metadata);

					updateCache(checksum, metadata);
				} catch (Exception e) {
					throw new FileStorageException("Failed to process file " + file.getName(), e);
				}
			}
		}
	}

	private boolean doesFilePreExistWithSameName(String checksum, String fileName) {
		List<FileMetadata> fileMetadataList = fileMetadataDAO.findByChecksum(checksum);
		LOGGER.info("File Content Available :{}", !ObjectUtils.isEmpty(fileMetadataList));
		if (!ObjectUtils.isEmpty(fileMetadataList)) {
			for (FileMetadata metaData : fileMetadataList) {
				if (metaData.getFileName().equals(fileName)) {
					return true;
				}
			}

		}
		return false;
	}

	private boolean allFilesEmpty(MultipartFile[] files) {
		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private String calculateChecksum(byte[] data) {
		return ChecksumCalculator.calculateChecksum(data);
	}

	private String calculateChecksum(File file) {
		return ChecksumCalculator.calculateChecksum(file);
	}

	@Override
	public void addFileMetadata(FileMetadata metadataDTO) {
		String checksum = metadataDTO.getChecksum();
		List<FileMetadata> existingFiles = fileMetadataDAO.findByChecksum(checksum);
		if (ObjectUtils.isEmpty(existingFiles)) {
			throw new FileStorageException("File with checksum " + checksum + " does not exist.");
		}
		String newFileName = metadataDTO.getFileName();
		// Ensure the new file name is unique among the files with the same checksum
		for (FileMetadata existingFile : existingFiles) {
			if (existingFile.getFileName().equals(newFileName)) {
				throw new FileStorageException("File with name " + newFileName + " already exists.");
			}
		}
		FileMetadata existingFile = existingFiles.get(0);

		long size = existingFile.getSize();
		LocalDateTime uploadedDate = LocalDateTime.now();

		FileMetadata newMetadata = new FileMetadata(newFileName, size, uploadedDate, checksum);
		fileMetadataDAO.save(newMetadata);

		try {
			Path sourcePath = Paths.get(UPLOAD_DIR, existingFile.getFileName());
			Path targetPath = Paths.get(UPLOAD_DIR, newFileName);
			Files.copy(sourcePath, targetPath);

			updateCache(checksum, newMetadata);
		} catch (IOException e) {
			throw new FileStorageException("Failed to create duplicate file " + newFileName, e);
		}
	}

	@Override
	public boolean removeFile(String fileName) {
		FileMetadata metadata = fileMetadataDAO.findByFileName(fileName);
		if (metadata == null) {
			throw new FileStorageException("File with name " + fileName + " does not exist.");
		}

		String checksum = metadata.getChecksum();

		boolean output = fileMetadataDAO.deleteByFileName(fileName);

		Path filePath = Paths.get(UPLOAD_DIR, fileName);
		try {
			Files.deleteIfExists(filePath);
		} catch (IOException e) {
			throw new FileStorageException("Failed to delete file " + fileName, e);
		}

		evictCache(checksum);
		return output;
	}

	@CacheEvict(value = "fileMetadataCache", key = "#checksum")
	public void evictCache(String checksum) {
	}

	@Override
	public String updateFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new FileStorageException("No file provided for update");
		}
		if (!validateFileExtension(file)) {
			throw new BadFileException("Invalid file extension. Allowed extensions are: " + ALLOWED_EXTENSIONS);
		}
		String outputMessage = "";
		String fileName = file.getOriginalFilename();
		long size = file.getSize();
		LocalDateTime uploadedDate = LocalDateTime.now();

		try {
			String newChecksum = calculateChecksum(file.getBytes());

			FileMetadata existingMetadata = fileMetadataDAO.findByFileName(fileName);
			if (existingMetadata != null) {
				String existingChecksum = existingMetadata.getChecksum();

				if (existingChecksum.equals(newChecksum)) {
					return outputMessage += "Same Content available in server,No update required";
				}
				Path uploadPath = Paths.get(UPLOAD_DIR, fileName);
				Files.copy(file.getInputStream(), uploadPath, StandardCopyOption.REPLACE_EXISTING);

				// Update metadata
				existingMetadata.setSize(size);
				existingMetadata.setUploadedDate(uploadedDate);
				existingMetadata.setChecksum(newChecksum);
				fileMetadataDAO.save(existingMetadata);

				updateCache(newChecksum, existingMetadata);
				outputMessage += "Update Successful";
			} else {
				addFiles(new MultipartFile[] { file });
				outputMessage += "New File created Successfully";
			}
		} catch (IOException e) {
			throw new FileStorageException("Failed to update file " + fileName, e);
		}
		return outputMessage;
	}

	private boolean validateFileExtension(MultipartFile file) {
		String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
		if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
			return false;
		}
		return true;
	}

	@Override
	public Map<String, Integer> wordCount() {
		Map<String, Integer> result = new HashMap<>();
		try {
			Files.walk(Paths.get(UPLOAD_DIR)).filter(Files::isRegularFile).forEach(file -> {
				result.compute("totalFiles", (k, v) -> v == null ? 1 : v + 1);
				result.compute("totalWordCount", (k, v) -> v == null ? countWordsInFile(file) : v + countWordsInFile(file));
			});

			System.out.println("Result: " + result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static int countWordsInFile(java.nio.file.Path filePath) {
		try {
			return (int) Files.lines(filePath).flatMap(line -> Stream.of(line.split("\\s+"))).count();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public Map<String, Integer> getFrequentWords(int limit, SortOrder order) {
		Map<String, Integer> wordFrequency = new ConcurrentHashMap<>();

		try {
			Files.walk(Paths.get(UPLOAD_DIR)).parallel().filter(Files::isRegularFile).forEach(file -> {
				try {
					prepareFrequentWordsMap(file, wordFrequency);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sortFrequentWords(wordFrequency, limit, order);
	}

	private void prepareFrequentWordsMap(Path filePath, Map<String, Integer> wordFrequency) throws IOException {
		Files.lines(filePath).flatMap(line -> Stream.of(line.split("\\s+"))).forEach(word -> wordFrequency.merge(word, 1, Integer::sum));
	}

	private Map<String, Integer> sortFrequentWords(Map<String, Integer> wordFrequency, int limit, SortOrder order) {
		Comparator<Map.Entry<String, Integer>> comparator = Comparator.comparing(Map.Entry::getValue);
		if (order == SortOrder.DESC) {
			comparator = comparator.reversed();
		}

		return wordFrequency.entrySet().stream().sorted(comparator).limit(limit)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}
}
