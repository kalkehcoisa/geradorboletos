package servlets;

import java.io.*;
import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.http.*;
import javax.servlet.*;

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
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static Boleto createBoleto(HttpServletRequest request)
    {
		
		//INFORMANDO DADOS SOBRE O CEDENTE.
		String cedente_nome = request.getParameter("cedente_nome");
		String cedente_cnpj = request.getParameter("cedente_cnpj");
        Cedente cedente = new Cedente(cedente_nome, cedente_cnpj);

        //INFORMANDO DADOS SOBRE O SACADO.
		String sacado_nome = request.getParameter("sacado_nome");
		String sacado_cpf = request.getParameter("sacado_cpf");
        Sacado sacado = new Sacado(sacado_nome, sacado_cpf);

      
        // Informando o endereço do sacado.
        String enderecosac_uf = request.getParameter("enderecosac_uf");
        String enderecosac_localidade = request.getParameter("enderecosac_localidade");
        String enderecosac_cep = request.getParameter("enderecosac_cep");
        String enderecosac_bairro = request.getParameter("enderecosac_bairro");
        String enderecosac_logradouro = request.getParameter("enderecosac_logradouro");
        String enderecosac_numero = request.getParameter("enderecosac_numero");

        Endereco enderecoSac = new Endereco();
        enderecoSac.setUF(UnidadeFederativa.valueOfSigla(enderecosac_uf));
        enderecoSac.setLocalidade(enderecosac_localidade);
        enderecoSac.setCep(new CEP(enderecosac_cep));
        enderecoSac.setBairro(enderecosac_bairro);
        enderecoSac.setLogradouro(enderecosac_logradouro);
        enderecoSac.setNumero(enderecosac_numero);
        sacado.addEndereco(enderecoSac);

        
        //INFORMANDO DADOS SOBRE O SACADOR AVALISTA.
    	String sacadoravalista_nome = request.getParameter("sacadoravalista_nome");
    	String sacadoravalista_cnpj = request.getParameter("sacadoravalista_cnpj");
        SacadorAvalista sacadorAvalista = new SacadorAvalista(sacadoravalista_nome, sacadoravalista_cnpj);

        //Informando o endereço do sacador avalista.
        String enderecosacaval_uf = request.getParameter("enderecosacaval_uf");
        String enderecosacaval_localidade = request.getParameter("enderecosacaval_localidade");
        String enderecosacaval_cep = request.getParameter("enderecosacaval_cep");
        String enderecosacaval_bairro = request.getParameter("enderecosacaval_bairro");
        String enderecosacaval_logradouro = request.getParameter("enderecosacaval_logradouro");
        String enderecosacaval_numero = request.getParameter("enderecosacaval_numero");
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
        String contabancaria = "BRADESCO";
        int contabancaria_numerodaconta = 123456;
        String contabancaria_numerodaconta_digito = "0";
        Integer contabancaria_carteira = 30;
        int contabancaria_agencia = 1234;
        String contabancaria_agencia_digito = "1";
        ContaBancaria contaBancaria = new ContaBancaria(BancosSuportados.BANCO_BRADESCO.create());
        contaBancaria.setNumeroDaConta(new NumeroDaConta(contabancaria_numerodaconta, contabancaria_numerodaconta_digito));
        contaBancaria.setCarteira(new Carteira(contabancaria_carteira));
        contaBancaria.setAgencia(new Agencia(contabancaria_agencia, contabancaria_agencia_digito));

        
        String titulo_numerododocumento = request.getParameter("titulo_numerododocumento");
        String titulo_nossonumero = request.getParameter("titulo_nossonumero");
        String titulo_digitodonossonumero = request.getParameter("titulo_digitodonossonumero");
        String titulo_valor = request.getParameter("titulo_valor");
        String titulo_datadodocumento = request.getParameter("titulo_datadodocumento");
        String titulo_datadovencimento = request.getParameter("titulo_datadovencimento");
        String titulo_desconto = request.getParameter("titulo_desconto");
        String titulo_deducao = request.getParameter("titulo_deducao");
        String titulo_mora = request.getParameter("titulo_mora");
        String titulo_acrecimo = request.getParameter("titulo_acrecimo");
        String titulo_valorcobrado = request.getParameter("titulo_valorcobrado");
        Titulo titulo = new Titulo(contaBancaria, sacado, cedente, sacadorAvalista);
        
        titulo.setNumeroDoDocumento(titulo_numerododocumento);
        titulo.setNossoNumero(titulo_nossonumero);
        titulo.setDigitoDoNossoNumero(titulo_digitodonossonumero);
        titulo.setValor(BigDecimal(titulo_valor));
        titulo.setDataDoDocumento(new Date(titulo_datadodocumento));
        titulo.setDataDoVencimento(new Date(titulo_datadovencimento));
        titulo.setTipoDeDocumento(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
        titulo.setAceite(EnumAceite.A);
        titulo.setDesconto(new BigDecimal(titulo_desconto));
        titulo.setDeducao(BigDecimal(titulo_deducao));
        titulo.setMora(BigDecimal(titulo_mora));
        titulo.setAcrecimo(BigDecimal(titulo_acrecimo));
        titulo.setValorCobrado(BigDecimal(titulo_valorcobrado));

        //INFORMANDO OS DADOS SOBRE O BOLETO.
        Boleto boleto = new Boleto(titulo);
        
        boleto.setLocalPagamento("Pagável preferencialmente na Rede X ou em " +
                        "qualquer Banco até o Vencimento.");
        boleto.setInstrucaoAoSacado("Senhor sacado, sabemos sim que o valor " +
                        "cobrado não é o esperado, aproveite o DESCONTÃO!");
        boleto.setInstrucao1("PARA PAGAMENTO 1 até Hoje não cobrar nada!");
        boleto.setInstrucao2("PARA PAGAMENTO 2 até Amanhã Não cobre!");
        boleto.setInstrucao3("PARA PAGAMENTO 3 até Depois de amanhã, OK, não cobre.");
        boleto.setInstrucao4("PARA PAGAMENTO 4 até 04/xx/xxxx de 4 dias atrás COBRAR O VALOR DE: R$ 01,00");
        boleto.setInstrucao5("PARA PAGAMENTO 5 até 05/xx/xxxx COBRAR O VALOR DE: R$ 02,00");
        boleto.setInstrucao6("PARA PAGAMENTO 6 até 06/xx/xxxx COBRAR O VALOR DE: R$ 03,00");
        boleto.setInstrucao7("PARA PAGAMENTO 7 até xx/xx/xxxx COBRAR O VALOR QUE VOCÊ QUISER!");
        boleto.setInstrucao8("APÓS o Vencimento, Pagável Somente na Rede X.");

		return boleto;
    }
	
	
	
	public void doGet (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		Boleto boleto = createBoleto(req);
		BoletoViewer viewer = new BoletoViewer(boleto);
		byte[] pdfAsBytes = viewer.getPdfAsByteArray();

		res.setContentType("application/pdf");
		res.setHeader("Content-Disposition", "attachment; filename=boleto.pdf");

		OutputStream output = res.getOutputStream();
		output.write(pdfAsBytes);

		res.flushBuffer();

		
		/*PrintWriter out = res.getWriter();
		out.println("Hello, Brave new World!");
		out.close();*/
	}

	public void doPost (HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException
	{
		Boleto boleto = createBoleto(req);
		BoletoViewer viewer = new BoletoViewer(boleto);
		byte[] pdfAsBytes = viewer.getPdfAsByteArray();

		res.setContentType("application/pdf");
		res.setHeader("Content-Disposition", "attachment; filename=boleto.pdf");

		OutputStream output = res.getOutputStream();
		output.write(pdfAsBytes);

		res.flushBuffer();

		
		/*PrintWriter out = res.getWriter();
		out.println("Hello, Brave new World!");
		out.close();*/
	}
}