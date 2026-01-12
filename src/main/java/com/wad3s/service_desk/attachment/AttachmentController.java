package com.wad3s.service_desk.attachment;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attachments")
public class AttachmentController {

    private final TicketAttachmentRepository attachmentRepository;
    private final FileStorageService fileStorageService;

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {

        TicketAttachment a = attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found: " + id));

        Resource file = fileStorageService.load(a.getStorageKey());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(a.getContentType()))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + a.getFilename() + "\""
                )
                .body(file);
    }
}
