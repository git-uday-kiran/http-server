package server.http;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UriMethodHandlerTest {

	@BeforeEach
	void setUp() {
	}

	@AfterEach
	void tearDown() {
	}

	@Test
	void cleanPath() {
		String[] paths1 = {"/one/{var}/two", "three/", "/four/"};
		String[] paths2 = {"one/", "/{var}/", "/two/", "/three/", "four//"};
		String[] paths3 = {"/one/", "/{var}/", "/two/", "/three/", "///four////"};
		String[] paths4 = {"one//", "////{var}//", "//two/", "/three///", "four"};
		String expected = "/one/{var}/two/three/four";
//		assertThat(handler.cleanPath(paths1)).isEqualTo(expected);
//		assertThat(handler.cleanPath(paths2)).isEqualTo(expected);
//		assertThat(handler.cleanPath(paths3)).isEqualTo(expected);
//		assertThat(handler.cleanPath(paths4)).isEqualTo(expected);
	}

	@Test
	void trimSlashes() {
		String path1 = "///one/two//";
		String path2 = "/one/two///";
		String path3 = "//";
		String path4 = "/";
		String path5 = "";

//		assertThat(handler.trimPathSeparator(path1)).isEqualTo("one/two");
//		assertThat(handler.trimPathSeparator(path2)).isEqualTo("one/two");
//		assertThat(handler.trimPathSeparator(path3)).isEqualTo("");
//		assertThat(handler.trimPathSeparator(path4)).isEqualTo("");
//		assertThat(handler.trimPathSeparator(path5)).isEqualTo("");
	}

	@Test
	void keepOnlyOneSeparator() {
		String path1 = "/one//two///three////";
		String path2 = "////one///two//three/";
		String path3 = "//one//two//three//";
		String path4 = "/one/two/three/";
		String path5 = "//";
		String path6 = "/";
		char delimiter = '/';

		String expected = "/one/two/three/";
//		assertThat(handler.keepOnlyOneSeparator(path1, delimiter)).isEqualTo(expected);
//		assertThat(handler.keepOnlyOneSeparator(path2, delimiter)).isEqualTo(expected);
//		assertThat(handler.keepOnlyOneSeparator(path3, delimiter)).isEqualTo(expected);
//		assertThat(handler.keepOnlyOneSeparator(path4, delimiter)).isEqualTo(expected);
//		assertThat(handler.keepOnlyOneSeparator(path5, delimiter)).isEqualTo("/");
//		assertThat(handler.keepOnlyOneSeparator(path6, delimiter)).isEqualTo("/");
	}

}