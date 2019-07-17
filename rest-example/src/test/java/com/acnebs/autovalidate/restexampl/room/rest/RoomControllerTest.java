package com.acnebs.autovalidate.restexampl.room.rest;


import com.acnebs.autovalidate.restexampl.room.domain.Room;
import com.acnebs.autovalidate.restexampl.room.repository.RoomRepositoryInMemImpl;
import com.fasterxml.jackson.databind.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private RoomRepositoryInMemImpl roomRepository;

    @Before
    public void init() {
        roomRepository.addRoom(
                Room.builder()
                        .number("E.11")
                        .feelGoodCapacity(2)
                        .maximumCapacity(3)
                        .build()
        );
    }

    @Test
    public void getAll_OK() throws Exception {

        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/rooms"))
                /*.andDo(print())*/
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                //.andExpect(jsonPath("$.number", is("E.11")))
                ;

        final MvcResult mvcResult = resultActions.andReturn();
        final String json = mvcResult.getResponse().getContentAsString();
        List<RoomResource> roomResources = RoomResource.listFromJson(json);
        assertEquals(1, roomResources.size());
        assertEquals("E.11", roomResources.get(0).getNumber());

    }

    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void putRoom_OK() throws Exception {
        RoomResource res = new RoomResource()
                .setNumber("E.12")
                .setFeelGoodCapacity(2)
                .setMaximumCapacity(3);

        Room room = Room.builder()
                .number("E.12")
                .feelGoodCapacity(2)
                .maximumCapacity(3)
                .build();


        mockMvc.perform(MockMvcRequestBuilders.get("/rooms/E.12"))
                //.andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                ;

        mockMvc.perform(
                    MockMvcRequestBuilders.put("/rooms/E.12")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(room))
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                //.andExpect(jsonPath("$.number", is("E.11")))
                ;


        final ResultActions resultActionsGet = mockMvc.perform(MockMvcRequestBuilders.get("/rooms/E.12"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                //.andExpect(jsonPath("$.number", is("E.11")))
                ;

        final MvcResult mvcResult = resultActionsGet.andReturn();
        final String json = mvcResult.getResponse().getContentAsString();
        Room roomAfterPut = Room.fromJson(json);
        assertEquals("E.12", roomAfterPut.getNumber());
        assertEquals(null, roomAfterPut.getName());
        assertEquals(Integer.valueOf(2), roomAfterPut.getFeelGoodCapacity());
        assertEquals(Integer.valueOf(3), roomAfterPut.getMaximumCapacity());
        /*RoomResource roomResource = RoomResource.fromJson(json);
        assertEquals("E.12", roomResource.getNumber());
        assertEquals(null, roomResource.getName());
        assertEquals(Integer.valueOf(2), roomResource.getFeelGoodCapacity());
        assertEquals(Integer.valueOf(3), roomResource.getMaximumCapacity());*/

    }

    @Test
    public void putRoom_NOK() throws Exception {
        RoomResource res = new RoomResource();

        mockMvc.perform(
                    MockMvcRequestBuilders.put("/rooms/XXX")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(res.toJson())
                )
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                //.andExpect(jsonPath("$.number", is("E.11")))
                ;
    }

}
