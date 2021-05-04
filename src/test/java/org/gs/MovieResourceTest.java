package org.gs;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class MovieResourceTest {

  @InjectMock MovieRepository movieRepository;
  @Inject MovieResource movieResource;

  private Movie movie;

  @BeforeEach
  void setUp() {
    movie = new Movie();
    movie.setTitle("FirstMovie");
    movie.setDescription("MyFirstMovie");
    movie.setCountry("Planet");
    movie.setDirector("Me");
    movie.setId(1L);
  }

  @Test
  void getAll() {
    List<Movie> movies = new ArrayList();
    movies.add(movie);
    Mockito.when(movieRepository.listAll()).thenReturn(movies);
    Response response = movieResource.getAll();
    assertNotNull(response);
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    List<Movie> entity = (List<Movie>) response.getEntity();
    assertFalse(entity.isEmpty());
    assertEquals("FirstMovie", entity.get(0).getTitle());
    assertEquals(1L, entity.get(0).getId());
    assertEquals("MyFirstMovie", entity.get(0).getDescription());
    assertEquals("Planet", entity.get(0).getCountry());
    assertEquals("Me", entity.get(0).getDirector());
  }

  @Test
  void getByIdOK() {
    Mockito.when(movieRepository.findByIdOptional(1L)).thenReturn(Optional.of(movie));

    Response response = movieResource.getById(1L);
    assertNotNull(response);
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    Movie entity = (Movie) response.getEntity();
    assertEquals("FirstMovie", entity.getTitle());
    assertEquals(1L, entity.getId());
    assertEquals("MyFirstMovie", entity.getDescription());
    assertEquals("Planet", entity.getCountry());
    assertEquals("Me", entity.getDirector());
  }

  @Test
  void getByIdKO() {
    Mockito.when(movieRepository.findByIdOptional(1L)).thenReturn(Optional.empty());

    Response response = movieResource.getById(1L);
    assertNotNull(response);
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    assertNull(response.getEntity());
  }

  @Test
  void getByTitleOK() {

    PanacheQuery<Movie> query = Mockito.mock(PanacheQuery.class);
    Mockito.when(query.page(Mockito.any())).thenReturn(query);
    Mockito.when(query.singleResultOptional()).thenReturn(Optional.of(movie));

    Mockito.when(movieRepository.find("title", "FirstMovie")).thenReturn(query);

    Response response = movieResource.getByTitle("FirstMovie");
    assertNotNull(response);
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    Movie entity = (Movie) response.getEntity();
    assertEquals("FirstMovie", entity.getTitle());
    assertEquals(1L, entity.getId());
    assertEquals("MyFirstMovie", entity.getDescription());
    assertEquals("Planet", entity.getCountry());
    assertEquals("Me", entity.getDirector());
  }

  @Test
  void getByTitleKO() {

    PanacheQuery<Movie> query = Mockito.mock(PanacheQuery.class);
    Mockito.when(query.page(Mockito.any())).thenReturn(query);
    Mockito.when(query.singleResultOptional()).thenReturn(Optional.empty());

    Mockito.when(movieRepository.find("title", "FirstMovie")).thenReturn(query);

    Response response = movieResource.getByTitle("FirstMovie");
    assertNotNull(response);
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    assertNull(response.getEntity());
  }

  @Test
  void getByCountry() {
    List<Movie> movies = new ArrayList();
    movies.add(movie);
    Mockito.when(movieRepository.findByCountry("Planet")).thenReturn(movies);

    Response response = movieResource.getByCountry("Planet");
    assertNotNull(response);
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    List<Movie> entity = (List<Movie>) response.getEntity();
    assertFalse(entity.isEmpty());
    assertEquals("FirstMovie", entity.get(0).getTitle());
    assertEquals(1L, entity.get(0).getId());
    assertEquals("MyFirstMovie", entity.get(0).getDescription());
    assertEquals("Planet", entity.get(0).getCountry());
    assertEquals("Me", entity.get(0).getDirector());
  }

  @Test
  void createOK() {

    Mockito.doNothing().when(movieRepository).persist(ArgumentMatchers.any(Movie.class));

    Mockito.when(movieRepository.isPersistent(ArgumentMatchers.any(Movie.class))).thenReturn(true);

    Movie newMovie = new Movie();
    newMovie.setTitle("SecondMovie");
    newMovie.setDescription("MySecondMovie");
    newMovie.setCountry("Planet");
    newMovie.setDirector("Me");
    Response response = movieResource.create(newMovie);
    assertNotNull(response);
    assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
    assertNotNull(response.getLocation());
    assertNull(response.getEntity());
  }

  @Test
  void createKO() {

    Mockito.doNothing().when(movieRepository).persist(ArgumentMatchers.any(Movie.class));

    Mockito.when(movieRepository.isPersistent(ArgumentMatchers.any(Movie.class))).thenReturn(false);

    Movie newMovie = new Movie();
    newMovie.setTitle("SecondMovie");
    newMovie.setDescription("MySecondMovie");
    newMovie.setCountry("Planet");
    newMovie.setDirector("Me");
    Response response = movieResource.create(newMovie);
    assertNotNull(response);
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    assertNull(response.getEntity());
    assertNull(response.getLocation());
  }

  @Test
  void updateByIdOK() {
    Movie updatedMovie = new Movie();
    updatedMovie.setTitle("SecondMovie");

    Mockito.when(movieRepository.findByIdOptional(1L)).thenReturn(Optional.of(movie));

    Response response = movieResource.updateById(1L, updatedMovie);

    assertNotNull(response);
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());
    Movie entity = (Movie) response.getEntity();
    assertEquals("SecondMovie", entity.getTitle());
    assertEquals(1L, entity.getId());
    assertEquals("MyFirstMovie", entity.getDescription());
    assertEquals("Planet", entity.getCountry());
    assertEquals("Me", entity.getDirector());
  }

  @Test
  void updateByIdKO() {

    Mockito.when(movieRepository.findByIdOptional(1L)).thenReturn(Optional.empty());

    Response response = movieResource.updateById(1L, new Movie());

    assertNotNull(response);
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    assertNull(response.getEntity());
  }

  @Test
  void deleteByIdOK() {
    Mockito.when(movieRepository.deleteById(1L)).thenReturn(true);
    Response response = movieResource.deleteById(1L);
    assertNotNull(response);
    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    assertNull(response.getEntity());
  }

  @Test
  void deleteByIdKO() {
    Mockito.when(movieRepository.deleteById(1L)).thenReturn(false);
    Response response = movieResource.deleteById(1L);
    assertNotNull(response);
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    assertNull(response.getEntity());
  }
}
