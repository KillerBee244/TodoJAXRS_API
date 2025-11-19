package com.myapp.struts.controller;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import javax.servlet.ServletContext;

@Path("/tasks")
public class TaskResource {

    @Context
    ServletContext context;

    private TaskService getService() {
        String realPath = context.getRealPath("/WEB-INF/tasks.xml");
        System.out.println("XML PATH = " + realPath);
        return new TaskService(realPath);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Task> getAll() {
        return getService().getAllTasks();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Task getById(@PathParam("id") int id) {
        return getService().findById(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(Task task) {
        System.out.println(">>> POST body mapped to Task = " + task);

        if (task == null) {
            // JSON không map được vào Task
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Request body không map được vào Task (task = null). Kiểm tra lại JSON hoặc model.\"}")
                    .build();
        }

        try {
            Task created = getService().addTask(task);
            if (created == null) {
                return Response.serverError()
                        .entity("{\"error\":\"addTask trả về null\"}")
                        .build();
            }
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"error\":\"Exception trong addTask: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, Task task) {
        System.out.println(">>> PUT body mapped to Task = " + task);

        if (task == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Request body không map được vào Task (task = null).\"}")
                    .build();
        }

        try {
            Task updated = getService().updateTask(id, task);
            if (updated == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Không tìm thấy Task id = " + id + "\"}")
                        .build();
            }
            return Response.ok(updated).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("{\"error\":\"Exception trong updateTask: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") int id) {
        boolean ok = getService().deleteTask(id);
        if (!ok) return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok().build();
    }
}
