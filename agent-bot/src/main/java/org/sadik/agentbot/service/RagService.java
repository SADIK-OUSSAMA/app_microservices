package org.sadik.agentbot.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RagService {

    private final VectorStore vectorStore;
    private final AtomicInteger documentCount = new AtomicInteger(0);

    @Value("${rag.vectorstore.path:./data/vectorstore.json}")
    private String vectorStorePath;

    public RagService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Ingest a document into the vector store
     */
    public int ingestDocument(byte[] content, String filename) {
        Resource resource = new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        // Parse document using Tika
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> documents = reader.get();

        // Split into chunks
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> chunks = splitter.apply(documents);

        // Add to vector store
        vectorStore.add(chunks);
        documentCount.addAndGet(chunks.size());

        // Persist to file
        saveVectorStore();

        return chunks.size();
    }

    /**
     * Search for relevant documents
     */
    public List<Document> search(String query, int topK) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(topK)
                        .build());
    }

    /**
     * Get context from relevant documents for RAG
     */
    public String getContext(String query) {
        List<Document> docs = search(query, 3);
        if (docs.isEmpty()) {
            return null;
        }

        StringBuilder context = new StringBuilder();
        for (Document doc : docs) {
            context.append(doc.getText()).append("\n\n");
        }
        return context.toString().trim();
    }

    /**
     * Clear all documents from the vector store
     */
    public void clearDocuments() {
        // Create a fresh vector store
        if (vectorStore instanceof SimpleVectorStore simpleStore) {
            // Delete the persistence file to clear
            File file = new File(vectorStorePath);
            if (file.exists()) {
                file.delete();
            }
        }
        documentCount.set(0);
    }

    /**
     * Check if any documents are indexed
     */
    public boolean hasDocuments() {
        return documentCount.get() > 0;
    }

    /**
     * Get document chunk count
     */
    public int getDocumentCount() {
        return documentCount.get();
    }

    private void saveVectorStore() {
        if (vectorStore instanceof SimpleVectorStore simpleStore) {
            File file = new File(vectorStorePath);
            file.getParentFile().mkdirs();
            simpleStore.save(file);
        }
    }
}
