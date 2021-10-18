package net.guides.springboot2.springboot2webappjsp.repositories;

import net.guides.springboot2.springboot2webappjsp.domain.Artifact;
import net.guides.springboot2.springboot2webappjsp.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public interface ArtifactRepository extends JpaRepository<Artifact, Integer> {

    //public List<Artifact> findByUser_id(Integer user_id);

    //search for all works of certain creator
    @Query(value = "select artifact_id, title, description, category_name, store_location, artifact_weights from artifact where user_id = ?1", nativeQuery = true)
    List<Map<String,Object>> findByUserId(Integer user_id);

    //search for all public works
    @Query(value = "select user_id, title, description, category_name, store_location from artifact where artifact_weights = 0", nativeQuery = true)
    List<Map<String,Object>> findByArtifactWeights();

    //search for all public works(distinct creator)
    @Query(value = "select user_id, title, description, category_name, store_location from artifact where artifact_weights = 0 group by user_id", nativeQuery = true)
    List<Map<String,Object>> findByDistinctUserId();


    @Query(value = "select * from artifact where user_id = ?1",nativeQuery = true)
    List<Artifact> findAllArtifact(Integer user_id);

    Artifact findArtifactByArtifactId(Integer artifact_id);

}
