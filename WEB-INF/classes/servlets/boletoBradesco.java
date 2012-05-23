package servlets;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;


import javax.servlet.http.*;
import javax.servlet.*;


import org.apache.commons.lang.RandomStringUtils;


import org.jrimum.bopepo.BancosSuportados;
import org.jrimum.bopepo.Boleto;
import org.jrimum.bopepo.view.BoletoViewer;
import org.jrimum.domkee.comum.pessoa.endereco.CEP;
import org.jrimum.domkee.comum.pessoa.endereco.Endereco;
import org.jrimum.domkee.comum.pessoa.endereco.UnidadeFederativa;
import org.jrimum.domkee.financeiro.banco.febraban.Agencia;
import org.jrimum.domkee.financeiro.banco.febraban.Carteira;
import org.jrimum.domkee.financeiro.banco.febraban.Cedente;
import org.jrimum.domkee.financeiro.banco.febraban.ContaBancaria;
import org.jrimum.domkee.financeiro.banco.febraban.NumeroDaConta;
import org.jrimum.domkee.financeiro.banco.febraban.Sacado;
import org.jrimum.domkee.financeiro.banco.febraban.SacadorAvalista;
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo.EnumAceite;


public class boletoBradesco extends HttpServlet
{
    /**
	 * Servlet responsavel por tratar da geracao e armazenamento de boletos do bradesco.
	 */
	private static final long serialVersionUID = 1L;
	private static final int PERMALINK_LENGTH = 40;
	private static String database = "geradorboletos";
	private static String user = "gerador";
	private static String password = "gerador";
	
	public static String recebePost(HttpServletRequest request) throws SQLException
    {
		/**
		 * Este metodo recebe os dados via post para a geracao de um boleto, os armazena no
		 * banco de dados e retorna o permalink para ser acesso via URL.
		 */
		String temp = "";
		String empresa_id = "1";
		Hashtable<String, String> dadosBoleto = new Hashtable<String, String>();
		
		//INFORMANDO DADOS SOBRE O CEDENTE.
		dadosBoleto.put("cedente_nome", request.getParameter("cedente_nome") );
		dadosBoleto.put("cedente_cnpj", request.getParameter("cedente_cnpj") );

        //INFORMANDO DADOS SOBRE O SACADO.
		dadosBoleto.put("sacado_nome", request.getParameter("sacado_nome") );
		dadosBoleto.put("sacado_cpf", request.getParameter("sacado_cpf") );
		
        // Informando o endereço do sacado.
        dadosBoleto.put("enderecosac_uf", request.getParameter("enderecosac_uf") );
        dadosBoleto.put("enderecosac_localidade", request.getParameter("enderecosac_localidade") );
        dadosBoleto.put("enderecosac_cep", request.getParameter("enderecosac_cep") );
        dadosBoleto.put("enderecosac_bairro", request.getParameter("enderecosac_bairro") );
        dadosBoleto.put("enderecosac_logradouro", request.getParameter("enderecosac_logradouro") );
        dadosBoleto.put("enderecosac_numero", request.getParameter("enderecosac_numero") );

        //INFORMANDO DADOS SOBRE O SACADOR AVALISTA.
    	dadosBoleto.put("sacadoravalista_nome", request.getParameter("sacadoravalista_nome") );
    	dadosBoleto.put("sacadoravalista_cnpj", request.getParameter("sacadoravalista_cnpj") );

        //Informando o endereço do sacador avalista.
    	temp = request.getParameter("enderecosacaval_uf");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("enderecosacaval_uf", temp );
        temp = request.getParameter("enderecosacaval_localidade");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("enderecosacaval_localidade", temp );
        temp = request.getParameter("enderecosacaval_cep");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("enderecosacaval_cep", temp );
        temp = request.getParameter("enderecosacaval_bairro");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("enderecosacaval_bairro", temp );
        temp = request.getParameter("enderecosacaval_logradouro");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("enderecosacaval_logradouro", temp );
        temp = request.getParameter("enderecosacaval_numero");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("enderecosacaval_numero", temp );

        //INFORMANDO OS DADOS SOBRE O TITULO.
        //Informando dados sobre a conta bancaria do titulo.
        dadosBoleto.put("contabancaria_numerodaconta", request.getParameter("contabancaria_numerodaconta") );
        dadosBoleto.put("contabancaria_numerodaconta_digito", request.getParameter("contabancaria_numerodaconta_digito") );
        dadosBoleto.put("contabancaria_carteira", request.getParameter("contabancaria_carteira") );
        dadosBoleto.put("contabancaria_agencia", request.getParameter("contabancaria_agencia") );
        dadosBoleto.put("contabancaria_agencia_digito", request.getParameter("contabancaria_agencia_digito") );

        //dadosBoleto.put("titulo_numerododocumento", request.getParameter("titulo_numerododocumento") );
        dadosBoleto.put("titulo_nossonumero", request.getParameter("titulo_nossonumero") );
        dadosBoleto.put("titulo_digitodonossonumero", request.getParameter("titulo_digitodonossonumero") );
        dadosBoleto.put("titulo_valor", request.getParameter("titulo_valor") );
        dadosBoleto.put("titulo_datadodocumento", request.getParameter("titulo_datadodocumento") );
        dadosBoleto.put("titulo_datadovencimento", request.getParameter("titulo_datadovencimento") );
        dadosBoleto.put("titulo_desconto", request.getParameter("titulo_desconto") );
        dadosBoleto.put("titulo_deducao", request.getParameter("titulo_deducao") );
        dadosBoleto.put("titulo_mora", request.getParameter("titulo_mora") );
        dadosBoleto.put("titulo_acrecimo", request.getParameter("titulo_acrecimo") );
        dadosBoleto.put("titulo_valorcobrado", request.getParameter("titulo_valorcobrado") );

        //INFORMANDO OS DADOS SOBRE O BOLETO.
        dadosBoleto.put("boleto_localpagamento", request.getParameter("boleto_localpagamento") );
        dadosBoleto.put("boleto_instrucaoaosacado", request.getParameter("boleto_instrucaoaosacado") );
        
        temp = request.getParameter("boleto_instrucao1");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("boleto_instrucao1", request.getParameter("boleto_instrucao1") );
        temp = request.getParameter("boleto_instrucao2");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("boleto_instrucao2", request.getParameter("boleto_instrucao2") );
        temp = request.getParameter("boleto_instrucao3");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("boleto_instrucao3", request.getParameter("boleto_instrucao3") );
        temp = request.getParameter("boleto_instrucao4");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("boleto_instrucao4", request.getParameter("boleto_instrucao4") );
        temp = request.getParameter("boleto_instrucao5");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("boleto_instrucao5", request.getParameter("boleto_instrucao5") );
        temp = request.getParameter("boleto_instrucao6");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("boleto_instrucao6", request.getParameter("boleto_instrucao6") );
        temp = request.getParameter("boleto_instrucao7");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("boleto_instrucao7", request.getParameter("boleto_instrucao7") );
        temp = request.getParameter("boleto_instrucao8");
        if( temp != null && temp.length() > 0 )
        	dadosBoleto.put("boleto_instrucao8", request.getParameter("boleto_instrucao8") );
        
        Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String permalink = RandomStringUtils.randomAlphanumeric(PERMALINK_LENGTH);
		
		conn = DriverManager.getConnection("jdbc:mysql://localhost/"+database+"?user="+user+"&password="+password);
		
		
		String campos = "(`permalink`, `empresa_id`, `";
		String valores = "(\'"+permalink+"\', \'"+empresa_id+"\', \'";
		Iterator<Map.Entry<String, String>> it = dadosBoleto.entrySet().iterator();
		int i = 0, size = dadosBoleto.size();
		while (it.hasNext())
		{
			i++;
			Map.Entry<String, String> entry = it.next();

			if( i == size )
			{
				campos = campos.concat( entry.getKey()+"`)" );
				valores = valores.concat( entry.getValue()+"\')" );
			}
			else
			{
				campos = campos.concat( entry.getKey()+"`, `" );
				valores = valores.concat( entry.getValue()+"\', \'" );
			}
		}

		//tenta inserir os dados no banco ate que o permalink gerado atual seja aceito 
		boolean teste_permalink = true;
		while( teste_permalink )
		{
		    stmt = conn.createStatement();
		    stmt.execute("SELECT COUNT(*) as count FROM `geradorboletos`.`boletobradesco` WHERE permalink=\'"+permalink+"\'");
		    rs = stmt.getResultSet();
	    	if( rs.next() && Integer.parseInt(rs.getString("count")) == 0)
	    	{
	    	    stmt = conn.createStatement();
			    stmt.executeUpdate("INSERT INTO `geradorboletos`.`boletobradesco` "+campos+" VALUES "+valores);
			    //conn.commit(); 
			    stmt.close();
			    conn.close();
			    
			    teste_permalink = false;
	    	}
	    	else
	    		permalink = RandomStringUtils.randomAlphanumeric(PERMALINK_LENGTH);
		}

		return permalink;
    }


	public static Boleto geraBoleto(String permalink) throws SQLException, NoBoletoException
    {
		/**
		 * Esta funcao recupera os dados do banco a partir do permalink e retorna o boleto 
		 * gerado usando esses dados.
		 */
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		conn = DriverManager.getConnection("jdbc:mysql://localhost/"+database+"?user="+user+"&password="+password);
		
		String[] valores = {
				"id", "cedente_nome", "cedente_cnpj", "sacado_nome", "sacado_cpf", 
				"enderecosac_uf", "enderecosac_localidade", "enderecosac_cep", 
				"enderecosac_bairro", "enderecosac_logradouro", "enderecosac_numero", 
				"sacadoravalista_nome", "sacadoravalista_cnpj", "enderecosacaval_uf", 
				"enderecosacaval_localidade", "enderecosacaval_cep", "enderecosacaval_bairro", 
				"enderecosacaval_logradouro", "enderecosacaval_numero", "contabancaria_numerodaconta", 
				"contabancaria_numerodaconta_digito", "contabancaria_carteira", "contabancaria_agencia", 
				"contabancaria_agencia_digito", "titulo_nossonumero", 
				"titulo_digitodonossonumero", "titulo_valor", "titulo_datadodocumento", "titulo_datadovencimento", 
				"titulo_desconto", "titulo_deducao", "titulo_mora", "titulo_acrecimo", "titulo_valorcobrado", 
				"boleto_localpagamento", "boleto_instrucaoaosacado", "boleto_instrucao1", "boleto_instrucao2", 
				"boleto_instrucao3", "boleto_instrucao4", "boleto_instrucao5", "boleto_instrucao6", 
				"boleto_instrucao7", "boleto_instrucao8", "permalink"};
		Hashtable<String, String> dados = new Hashtable<String, String>();
		String temp = "";
		//recupera os dados do banco e os armazena numa hashtable
	    stmt = conn.createStatement();
	    if( stmt.execute("SELECT * FROM `geradorboletos`.`boletobradesco` WHERE permalink=\'"+permalink+"\'") )
	    {
	        rs = stmt.getResultSet();
	        while( rs.next() )
	        	for(int i=0;i<valores.length;i++)
	        		try
	        		{
	        			dados.put(valores[i], rs.getString(valores[i]));
	        		}
	        		catch(Exception e)
	        		{
	        			temp += " |"+valores[i]+"| ";
	        		}
	        		if( temp.length() > 0 )
	        			throw new NoBoletoException(temp);
	        valores = null;
	    }
		
	    //verifica se a hashtable esta vazia, se esta, e porque nao ha nenhum boleto armazenado no banco
	    //para esse permalink
	    if(dados.isEmpty())
	    	throw new NoBoletoException("0: Boleto nao encontrado!");

		/**
		 * Inicia a geracao do boleto.
		 */
		//INFORMANDO DADOS SOBRE O CEDENTE.
		String cedente_nome = dados.get("cedente_nome");
		String cedente_cnpj = dados.get("cedente_cnpj");
        Cedente cedente = new Cedente(cedente_nome, cedente_cnpj);

        //INFORMANDO DADOS SOBRE O SACADO.
		String sacado_nome = dados.get("sacado_nome");
		String sacado_cpf = dados.get("sacado_cpf");
        Sacado sacado = new Sacado(sacado_nome, sacado_cpf);

      
        // Informando o endereço do sacado.
        String enderecosac_uf = dados.get("enderecosac_uf");
        String enderecosac_localidade = dados.get("enderecosac_localidade");
        String enderecosac_cep = dados.get("enderecosac_cep");
        String enderecosac_bairro = dados.get("enderecosac_bairro");
        String enderecosac_logradouro = dados.get("enderecosac_logradouro");
        String enderecosac_numero = dados.get("enderecosac_numero");

        Endereco enderecoSac = new Endereco();
        enderecoSac.setUF(UnidadeFederativa.valueOfSigla(enderecosac_uf));
        enderecoSac.setLocalidade(enderecosac_localidade);
        enderecoSac.setCep(new CEP(enderecosac_cep));
        enderecoSac.setBairro(enderecosac_bairro);
        enderecoSac.setLogradouro(enderecosac_logradouro);
        enderecoSac.setNumero(enderecosac_numero);
        sacado.addEndereco(enderecoSac);
        
        //INFORMANDO DADOS SOBRE O SACADOR AVALISTA.
    	String sacadoravalista_nome = dados.get("sacadoravalista_nome");
    	String sacadoravalista_cnpj = dados.get("sacadoravalista_cnpj");
        SacadorAvalista sacadorAvalista = new SacadorAvalista(sacadoravalista_nome, sacadoravalista_cnpj);

        //Informando o endereço do sacador avalista.
        String enderecosacaval_uf = dados.get("enderecosacaval_uf");
        String enderecosacaval_localidade = dados.get("enderecosacaval_localidade");
        String enderecosacaval_cep = dados.get("enderecosacaval_cep");
        String enderecosacaval_bairro = dados.get("enderecosacaval_bairro");
        String enderecosacaval_logradouro = dados.get("enderecosacaval_logradouro");
        String enderecosacaval_numero = dados.get("enderecosacaval_numero");
        Endereco enderecoSacAval = new Endereco();
        enderecoSacAval.setUF(UnidadeFederativa.valueOfSigla(enderecosacaval_uf));
        enderecoSacAval.setLocalidade(enderecosacaval_localidade);
        enderecoSacAval.setCep(new CEP(enderecosacaval_cep));
        enderecoSacAval.setBairro(enderecosacaval_bairro);
        enderecoSacAval.setLogradouro(enderecosacaval_logradouro);
        enderecoSacAval.setNumero(enderecosacaval_numero);
        sacadorAvalista.addEndereco(enderecoSacAval);

        //INFORMANDO OS DADOS SOBRE O TITULO.
        //Informando dados sobre a conta bancaria do titulo.
        //String contabancaria = "BRADESCO";
        int contabancaria_numerodaconta = Integer.parseInt( dados.get("contabancaria_numerodaconta") );
        String contabancaria_numerodaconta_digito = dados.get("contabancaria_numerodaconta_digito");
        Integer contabancaria_carteira = Integer.parseInt( dados.get("contabancaria_carteira") );
        int contabancaria_agencia = Integer.parseInt( dados.get("contabancaria_agencia") );
        String contabancaria_agencia_digito = dados.get("contabancaria_agencia_digito");
        ContaBancaria contaBancaria = new ContaBancaria(BancosSuportados.BANCO_BRADESCO.create());
        contaBancaria.setNumeroDaConta(new NumeroDaConta(contabancaria_numerodaconta, contabancaria_numerodaconta_digito));
        contaBancaria.setCarteira(new Carteira(contabancaria_carteira));
        contaBancaria.setAgencia(new Agencia(contabancaria_agencia, contabancaria_agencia_digito));

        //#Número de controle do cedente para o referido título. Não confundir com o nosso número. 
        String titulo_numerododocumento = "50000";//dados.get("id");
        //Código fornecido pelo Banco para identificação do título ou identificação 
        //do título atribuído pelo emissor do título de cobrança. 
        String titulo_nossonumero = dados.get("titulo_nossonumero");
        String titulo_digitodonossonumero = dados.get("titulo_digitodonossonumero");
        String titulo_valor = dados.get("titulo_valor");
        String titulo_datadodocumento = dados.get("titulo_datadodocumento");
        String titulo_datadovencimento = dados.get("titulo_datadovencimento");
        String titulo_desconto = dados.get("titulo_desconto");
        String titulo_deducao = dados.get("titulo_deducao");
        String titulo_mora = dados.get("titulo_mora");
        String titulo_acrecimo = dados.get("titulo_acrecimo");
        String titulo_valorcobrado = dados.get("titulo_valorcobrado");
        Titulo titulo = new Titulo(contaBancaria, sacado, cedente, sacadorAvalista);
        
        titulo.setNumeroDoDocumento(titulo_numerododocumento);
        titulo.setNossoNumero(titulo_nossonumero);
        titulo.setDigitoDoNossoNumero(titulo_digitodonossonumero);
        titulo.setValor(BigDecimal(titulo_valor));
        titulo.setDataDoDocumento(Date(titulo_datadodocumento));
        titulo.setDataDoVencimento(Date(titulo_datadovencimento));
        titulo.setTipoDeDocumento(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
        titulo.setAceite(EnumAceite.A);
        titulo.setDesconto(new BigDecimal(titulo_desconto));
        titulo.setDeducao(BigDecimal(titulo_deducao));
        titulo.setMora(BigDecimal(titulo_mora));
        titulo.setAcrecimo(BigDecimal(titulo_acrecimo));
        titulo.setValorCobrado(BigDecimal(titulo_valorcobrado));

        //INFORMANDO OS DADOS SOBRE O BOLETO.
        Boleto boleto = new Boleto(titulo);
        
        String boleto_localpagamento = dados.get("boleto_localpagamento");
		String boleto_instrucaoaosacado = dados.get("boleto_instrucaoaosacado");
		String boleto_instrucao1 = dados.get("boleto_instrucao1");
    	String boleto_instrucao2 = dados.get("boleto_instrucao2");
    	String boleto_instrucao3 = dados.get("boleto_instrucao3");
    	String boleto_instrucao4 = dados.get("boleto_instrucao4");
    	String boleto_instrucao5 = dados.get("boleto_instrucao5");
    	String boleto_instrucao6 = dados.get("boleto_instrucao6");
    	String boleto_instrucao7 = dados.get("boleto_instrucao7");
    	String boleto_instrucao8 = dados.get("boleto_instrucao8");
        boleto.setLocalPagamento(boleto_localpagamento);
        boleto.setInstrucaoAoSacado(boleto_instrucaoaosacado);
        boleto.setInstrucao1(boleto_instrucao1);
        boleto.setInstrucao1(boleto_instrucao2);
        boleto.setInstrucao1(boleto_instrucao3);
        boleto.setInstrucao1(boleto_instrucao4);
        boleto.setInstrucao1(boleto_instrucao5);
        boleto.setInstrucao1(boleto_instrucao6);
        boleto.setInstrucao1(boleto_instrucao7);
        boleto.setInstrucao1(boleto_instrucao8);
		/**
		 * Termina a geracao do boleto.
		 */
        
		return boleto;
    }
	
	
	private static BigDecimal BigDecimal(String value)
	{
		//Metodo para transformar strings em BigDecimal
		//feito pois a classe BigDecimal nao possui tal metodo
		return BigDecimal.valueOf( Double.parseDouble(value) );
	}

	private static Date Date(String data)
	{
		//Metodo para transformar strings em Date
		//feito pois a classe Date nao possui tal metodo (descontinuado)
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try
		{
			Date date = sdf.parse(data);
			return date;
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Date();
		}
	}



	public void doGet (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		String permalink = req.getPathInfo().replaceAll("/", "");
		OutputStream output = res.getOutputStream();
		
		req.setCharacterEncoding("UTF-8");

		if( permalink.length() == PERMALINK_LENGTH )
		{ //se o permalink tem o tamanho correto comeca a processar
			Boleto boleto = null;
			String saida = "";
			try
			{
				boleto = geraBoleto(permalink);
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				saida = e.getMessage();
			}
			catch( NoBoletoException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				saida = e.getMessage();
			}
			if( boleto == null )
			{
				output.write(saida.getBytes());
			}
			else
			{
				BoletoViewer viewer = new BoletoViewer(boleto);
				byte[] pdfAsBytes = viewer.getPdfAsByteArray();
		
				res.setContentType("application/pdf");
				res.setHeader("Content-Disposition", "attachment; filename=boleto.pdf");
		
				output.write(pdfAsBytes);
			}
			res.flushBuffer();
		}
		else //se o permalink nao tiver o tamanho correto
		{
			output.write(("0: Permalink invalido! Esperado comprimento 40, obtido comprimento "+permalink.length()+"!")
				  .getBytes());
			res.flushBuffer();
		}
		output.close();
	}

	public void doPost (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		String permalink = req.getPathInfo().replaceAll("/", "");
		OutputStream output = res.getOutputStream();
		
		req.setCharacterEncoding("UTF-8"); 
		
		if( permalink.length() == PERMALINK_LENGTH )
		{
			Boleto boleto = null;
			String saida = "";
			try
			{
				boleto = geraBoleto(permalink);
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				saida = e.getMessage();
			}
			catch( NoBoletoException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				saida = e.getMessage();
			}
			if( boleto == null )
			{
				output.write(saida.getBytes());
			}
			else
			{
				BoletoViewer viewer = new BoletoViewer(boleto);
				byte[] pdfAsBytes = viewer.getPdfAsByteArray();
		
				res.setContentType("application/pdf");
				res.setHeader("Content-Disposition", "attachment; filename=boleto.pdf");
				output.write(pdfAsBytes);
			}
			res.flushBuffer();
		}
		else
		{
			String saida;
			try
			{
				saida = recebePost(req);
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				saida = e.getMessage();
			}
	
			output.write(saida.getBytes());
	
			res.flushBuffer();
		}
		output.close();
	}
}