package br.edu.iftm;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/fii")

public class CadastroFundoImobiliarioServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        // Exemplo de verificação de login em outro servlet
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("login");
            return;
        }

        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DatabaseConnection.getConnection();
            // Executa consulta no banco

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM fundos_imobiliarios");
            ResultSet rs = ps.executeQuery();

            out.println("<html><head><title>Lista de Fundos Imobiliários</title></head><body>");
            out.println("<h3>Lista de Fundos Imobiliários</h3>");
            out.println(
                    "<table border='1'><tr><th>ID</th><th>Nome</th><th>Setor</th><th>Preço</th><th>Data IPO</th><th>Ações</th></tr>");

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String setor = rs.getString("setor");
                double preco = rs.getDouble("preco");
                Date dataFormatada = rs.getDate("data_ipo");

                // Busca os atributos no banco

                out.println("<tr><td>" + id + "</td><td>" + nome + "</td><td>" + setor + "</td><td>" + preco
                        + "</td><td>" + dataFormatada + "</td>");
                out.println("<td><form method='post' action='fii'>");
                out.println("<input type='hidden' name='acao' value='excluir'>");
                out.println("<input type='hidden' name='id' value='" + id + "'>");
                out.println("<input type='submit' value='Excluir'>");
                out.println("</form></td></tr>");
            }

            out.println("</table>");
            out.println("<a href='formulario.html'>Cadastrar</a>");
            out.println("</body></html>");
        } catch (SQLException | ClassNotFoundException e) {
            out.println(
                    "<div class='error-message'>Erro ao listar os fundos imobiliários: " + e.getMessage() + "</div>");
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String acao = request.getParameter("acao");
        if ("excluir".equals(acao)) {
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.trim().isEmpty()) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn = DatabaseConnection.getConnection();

                    int id = Integer.parseInt(idStr);

                    PreparedStatement ps = conn.prepareStatement("DELETE * FROM fundos_imobiliarios WHERE id = ?");
                    ps.setInt(1, id);
                    int rowsAffected = ps.executeUpdate();

                } catch (SQLException | ClassNotFoundException | NumberFormatException e) {
                    out.println("<div class='error-message'>Erro ao excluir fundo: " + e.getMessage() + "</div>");
                    e.printStackTrace();
                }
            }

        } else if ("cadastrar".equals(acao)) {
            String nome = request.getParameter("nome");
            String setor = request.getParameter("setor");
            String precoStr = request.getParameter("preco");
            String dataIpoStr = request.getParameter("data_ipo");

            if (nome == null || nome.trim().isEmpty() || setor == null || setor.trim().isEmpty() ||
                    precoStr == null || precoStr.trim().isEmpty() || dataIpoStr == null
                    || dataIpoStr.trim().isEmpty()) {
                out.println("<div class='error-message'>Todos os campos são obrigatórios.</div>");
                return;
            }

            double preco;
            try {
                preco = Double.parseDouble(precoStr);
            } catch (NumberFormatException e) {
                out.println("<div class='error-message'>Preço inválido.</div>");
                return;
            }

            Date dataIpo;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dataIpo = sdf.parse(dataIpoStr);
            } catch (ParseException e) {
                out.println("<div class='error-message'>Data de IPO inválida. Use o formato AAAA-MM-DD.</div>");
                return;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DatabaseConnection.getConnection();

                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO fundos_imobiliarios (nome, setor, preco, data_ipo) VALUES (?, ?, ?, ?)");
                ps.setString(1, nome);
                ps.setString(2, setor);
                ps.setDouble(3, preco);
                ps.setDate(4, new java.sql.Date(dataIpo.getTime()));
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected > 0) {
                    out.println("<div class='success-message'>Fundo imobiliário cadastrado com sucesso!</div>");
                    out.println("<a href='fii'>Voltar à lista</a>");
                } else {
                    out.println("<div class='error-message'>Erro ao cadastrar fundo imobiliário.</div>");
                }
            } catch (SQLException | ClassNotFoundException e) {
                out.println("<div class='error-message'>Erro ao cadastrar fundo: " + e.getMessage() + "</div>");
                e.printStackTrace();
            }
        }
    }
}