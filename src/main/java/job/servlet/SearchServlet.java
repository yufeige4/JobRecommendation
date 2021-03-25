package job.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import job.db.MySQLConnection;
import job.entity.Item;
import job.entity.ResultResponse;
import job.external.GitHubClient;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebServlet(name = "SearchServlet",  urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        ObjectMapper mapper = new ObjectMapper();
        if (session == null) {
            response.setStatus(403);
            mapper.writeValue(response.getWriter(), new ResultResponse("Session Invalid"));
            return;
        }
        String userId = request.getParameter("user_id");

        double lat = Double.parseDouble(request.getParameter("lat"));
        double lon = Double.parseDouble(request.getParameter("lon"));

        MySQLConnection connection = new MySQLConnection();
        Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
        connection.close();

        GitHubClient client = new GitHubClient();

        List<Item> items = client.search(lat, lon, null);

        for (Item item : items) {
            item.setFavorite(favoritedItemIds.contains(item.getId()));
        }

        response.setContentType("application/json");
        mapper.writeValue(response.getWriter(), items);


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        ObjectMapper mapper = new ObjectMapper();
        if (session == null) {
            response.setStatus(403);
            mapper.writeValue(response.getWriter(), new ResultResponse("Session Invalid"));
            return;
        }
    }
}
