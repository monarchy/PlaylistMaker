package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.data.search.StoreCleanerRepository
import com.example.playlistmaker.domain.search.StoreCleanerInterractor

class StoreCleanerInterractorImpl(private val storeCleanerRepository: StoreCleanerRepository):
    StoreCleanerInterractor {
    override fun execute() {
        storeCleanerRepository.cleanStore()
    }
}