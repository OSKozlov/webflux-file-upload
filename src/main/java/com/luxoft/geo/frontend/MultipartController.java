/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.luxoft.geo.frontend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class MultipartController {

	@PostMapping(value = "/upload", 
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
			produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	Flux<String> upload(@RequestBody Flux<Part> parts) {
		return parts
				.filter(p -> p instanceof FilePart)
				.cast(FilePart.class)
				.flatMap(this::saveFile)
				.map(File::getName);
	}
	
	private Mono<File> saveFile(FilePart filePart) {
		Path target = Paths.get("").resolve(filePart.filename());
		try {
			Files.deleteIfExists(target);
			File file = Files.createFile(target).toFile();
			return filePart.transferTo(file).map(r -> file);
		} catch (IOException e) {
//			log.info("An error occurred while saving the file...");
			return Mono.empty();
		}
	}
}
