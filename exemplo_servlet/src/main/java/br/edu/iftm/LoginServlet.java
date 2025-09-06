package br.edu.iftm;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Login</title></head><body>");
        out.println("<h3>Login</h3>");
        out.println("<form method='post' action='login'>");
        out.println("Usuário: <input type='text' name='usuario'><br>");
        out.println("Senha: <input type='password' name='senha'><br>");
        out.println("<input type='submit' value='Entrar'>");
        out.println("</form>");
        out.println("</body></html>");

        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String usuario = req.getParameter("usuario");
        String senha = req.getParameter("senha");

        resp.setContentType("text/html; charset=UTF-8");

        if ("admin".equals(usuario) && "password".equals(senha)) {
            // Cria ou recupera a sessão do usuário
            HttpSession session = req.getSession();
            session.setAttribute("usuarioLogado", usuario);

            resp.sendRedirect("fii");
        } else {
            resp.sendRedirect("login");
        }
    }

}
