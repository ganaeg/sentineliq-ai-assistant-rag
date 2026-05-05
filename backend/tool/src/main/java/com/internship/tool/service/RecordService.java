package com.internship.tool.service;

import com.internship.tool.entity.Record;
import com.internship.tool.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecordService {

    @Autowired
    private RecordRepository repository;

    // ✅ CREATE
    public Record save(Record record) {
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        return repository.save(record);
    }

    // ✅ UPDATE
    public Record update(Long id, Record newRecord) {
        Record existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        existing.setTitle(newRecord.getTitle());
        existing.setDescription(newRecord.getDescription());
        existing.setStatus(newRecord.getStatus());
        existing.setPriority(newRecord.getPriority());
        existing.setDueDate(newRecord.getDueDate());
        existing.setUpdatedAt(LocalDateTime.now());

        return repository.save(existing);
    }

    // ✅ DELETE (Soft)
    public Record softDelete(Long id) {
        Record record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        record.setStatus("DELETED");
        record.setUpdatedAt(LocalDateTime.now());

        return repository.save(record);
    }

    // ✅ SEARCH
    public List<Record> search(String keyword) {
        return repository.searchByTitle(keyword);
    }

    // ✅ FILTER (Day 9 Dev 3)
    public List<Record> filter(String status, LocalDate startDate, LocalDate endDate) {
        return repository.filterRecords(status, startDate, endDate);
    }

    // ✅ STATS
    public Map<String, Long> getStats() {
        List<Record> all = repository.findAll();

        long total = all.size();
        long open = all.stream().filter(r -> "OPEN".equalsIgnoreCase(r.getStatus())).count();
        long closed = all.stream().filter(r -> "CLOSED".equalsIgnoreCase(r.getStatus())).count();
        long deleted = all.stream().filter(r -> "DELETED".equalsIgnoreCase(r.getStatus())).count();

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("open", open);
        stats.put("closed", closed);
        stats.put("deleted", deleted);

        return stats;
    }

    // ✅ PAGINATION + SORTING
    public Page<Record> getAllRecords(int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return repository.findAll(pageable);
    }

    // ✅ CSV EXPORT
    public List<Record> getAllRecordsForExport() {
        return repository.findAll();
    }
}