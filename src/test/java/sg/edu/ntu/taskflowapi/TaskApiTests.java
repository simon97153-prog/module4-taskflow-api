package sg.edu.ntu.taskflowapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskApiTests {
    @Autowired
    private MockMvc mvc;

    @Test
    void taskLifecycle() throws Exception {
        String location = mvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":9999,\"title\":\"Study Java\",\"description\":\"Practice layers\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(org.hamcrest.Matchers.not(9999)))
                .andExpect(jsonPath("$.completed").value(false))
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title").value(org.hamcrest.Matchers.hasItem("Study Java")));
        mvc.perform(get(location)).andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Practice layers"));
        mvc.perform(put(location).contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test API\",\"description\":\"Use Postman\",\"completed\":false}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Test API"));
        mvc.perform(put(location + "/complete"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.completed").value(true));
        mvc.perform(get(location)).andExpect(jsonPath("$.completed").value(true));
        mvc.perform(delete(location)).andExpect(status().isOk());
        mvc.perform(get(location)).andExpect(status().isNotFound());
    }

    @Test
    void missingTasksReturn404() throws Exception {
        mvc.perform(get("/api/tasks/999999")).andExpect(status().isNotFound());
        mvc.perform(put("/api/tasks/999999").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Missing\"}")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/tasks/999999")).andExpect(status().isNotFound());
        mvc.perform(put("/api/tasks/999999/complete")).andExpect(status().isNotFound());
    }

    @Test
    void invalidRequestsReturn400() throws Exception {
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\" \"}")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/tasks/not-a-number")).andExpect(status().isBadRequest());
    }
}
