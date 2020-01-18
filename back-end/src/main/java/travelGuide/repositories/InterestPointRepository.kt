package travelGuide.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import travelGuide.collections.InterestPoint

interface InterestPointRepository : MongoRepository<InterestPoint?, String?>, InterestPointRepositoryCustom {}