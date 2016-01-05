
package danfenfe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Junior Tada
 * 
 * Ex: java -jar danfenfe.jar nfce_autorizada.xml
 */

public class DanfeNfe {
    
    private static HashMap map = new HashMap();

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ParseException{
        String xml_nfe = null;
        String qrcode = null;
        String p = null;
        String logo = null;
        String prestador_fantasia = "";
        String tomador_fantasia = "";
        String path = System.getProperty("user.dir");
        Path danfe_nfce_80 = Paths.get(path, "danfe","danfe_nfce_80.jasper");
        String url = danfe_nfce_80.toAbsolutePath().toString();
        
        // Default impressao em papel 80mm para quando for compilar
        // String jrxml = "/home/junior/JaspersoftWorkspace/MyReports/danfe_nfce_80.jrxml";
        for(String arg: args){
            if(arg.contains(".xml")){
                xml_nfe = arg;
            }
            if(arg.contains("qrcode=")){
                qrcode = arg.replace("qrcode=", "");
            }
            if(arg.contains("url=")){
                url = arg.replace("url=", "");
            }
            if(arg.contains("logo=")){
                logo = arg.replace("logo=", "");
            }
            if(arg.contains("prestador=")){
                prestador_fantasia = arg.replace("prestador=", "");
            }
            if(arg.contains("tomador=")){
                // nome fantasia do tomador/cliente
                tomador_fantasia = arg.replace("tomador=", "");
            }
            if(arg.contains("p=")){
                p = arg.replace("p=", "");
            switch (p){
                case "58mm":
                    //jrxml = "/home/junior/JaspersoftWorkspace/MyReports/danfe_nfce_58.jrxml";
                    Path danfe_nfce_58 = Paths.get(path, "danfe","danfe_nfce_58.jasper");
                    url =  danfe_nfce_58.toAbsolutePath().toString();
                    break;
                case "A4":
                    //jrxml = "/home/junior/JaspersoftWorkspace/MyReports/danfe_nfce_A4.jrxml";
                    Path danfe_nfce_A4 = Paths.get(path, "danfe","danfe_nfce_A4.jasper");
                    url =  danfe_nfce_A4.toAbsolutePath().toString();
                    break;
                case "nfe":
                    //nfe A4 retrato
                    Path nfe = Paths.get(path, "danfe","danfeR.jasper");
                    url =  nfe.toAbsolutePath().toString();
                    // teste
                    //url = "/home/junior/JaspersoftWorkspace/MyReports/danfeR.jasper";
                    break;
                case "nfse":
                    //nfse A4 retrato
                    Path nfse = Paths.get(path, "danfe","danfe_nfse.jasper");
                    url =  nfse.toAbsolutePath().toString();
                    break;
                default:
                    //jrxml = "/home/junior/JaspersoftWorkspace/MyReports/danfe_nfce_80.jrxml";
                    url =  danfe_nfce_80.toAbsolutePath().toString();
                    break;
                }
            }
        }
        // Leitura de arquivo para xml de nfe
        if(!p.equals("nfse")){
            File inputFile = new File(xml_nfe);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document xml = builder.parse(inputFile);
            xml.getDocumentElement().normalize();
            // Dados do emitente
            NodeList emitList = xml.getElementsByTagName("emit");
            Node emitNode = emitList.item(0);
            Element emitElement = (Element) emitNode;
            List <String> emit = new ArrayList<>();
            emit.add(emitElement.getElementsByTagName("xNome").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("CNPJ").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("IE").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("xLgr").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("nro").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("xBairro").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("xMun").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("UF").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("CEP").item(0).getTextContent());
            //for(int i = 0; i < emit.size(); i++){
            //    System.out.println(emit.get(i)+" "+i);
            //}
            List <String> dest = new ArrayList<>();

            // Dados do Destinatário
            NodeList destList = xml.getElementsByTagName("dest");
            if(destList.getLength() > 0){
                Node destNode = destList.item(0);
                Element destElement = (Element) destNode;
                try {
                    dest.add("CPF: "+destElement.getElementsByTagName("CPF").item(0).getTextContent());
                } catch (Exception e) {
                    dest.add("CNPJ: "+destElement.getElementsByTagName("CNPJ").item(0).getTextContent());
                }
                if(p.equalsIgnoreCase("nfe")){
                    dest.add(destElement.getElementsByTagName("xNome").item(0).getTextContent());
                    dest.add(destElement.getElementsByTagName("xLgr").item(0).getTextContent());
                    dest.add(destElement.getElementsByTagName("nro").item(0).getTextContent());
                    dest.add(destElement.getElementsByTagName("xBairro").item(0).getTextContent());
                    dest.add(destElement.getElementsByTagName("xMun").item(0).getTextContent());
                    dest.add(destElement.getElementsByTagName("UF").item(0).getTextContent());
                    dest.add(destElement.getElementsByTagName("CEP").item(0).getTextContent());
                }

                //for(int i = 0; i < dest.size(); i++){
                //    System.out.println(dest.get(i)+" "+i);
                //}
            }       

            // Dados dos Itens
            Collection <Item>  itens = new ArrayList<Item>();
            NodeList itensList = xml.getElementsByTagName("det");
            for(int i = 0; i < itensList.getLength(); i++){
                Node itensNode = itensList.item(i);
                Element itensElement = (Element) itensNode;
                Item item = new Item();
                item.setCodigo(itensElement.getElementsByTagName("cProd").item(0).getTextContent());
                item.setDescricao(itensElement.getElementsByTagName("xProd").item(0).getTextContent());
                item.setNcm(itensElement.getElementsByTagName("NCM").item(0).getTextContent());
                item.setCfop(itensElement.getElementsByTagName("CFOP").item(0).getTextContent());
                item.setQuantidade(itensElement.getElementsByTagName("qCom").item(0).getTextContent());
                item.setUnidade(itensElement.getElementsByTagName("uCom").item(0).getTextContent());
                item.setTotal(itensElement.getElementsByTagName("vUnCom").item(0).getTextContent());
                item.setValor(itensElement.getElementsByTagName("vProd").item(0).getTextContent());
                // csosn
                item.setCsosn(itensElement.getElementsByTagName("CSOSN").item(0).getTextContent());
                itens.add(item);
            }
            JRDataSource jr = new JRBeanCollectionDataSource(itens);
            // Dados da nota
            List <String> nota = new ArrayList<>();
            // Total
            NodeList totList = xml.getElementsByTagName("ICMSTot");
            Node totNode = totList.item(0);
            Element totElement = (Element) totNode;
            nota.add(totElement.getElementsByTagName("vNF").item(0).getTextContent());
            // Numero, Serie, Emissao
            NodeList ideList = xml.getElementsByTagName("ide");
            Node ideNode = ideList.item(0);
            Element ideElement = (Element) ideNode;
            nota.add(ideElement.getElementsByTagName("nNF").item(0).getTextContent());          // Numero
            nota.add(ideElement.getElementsByTagName("serie").item(0).getTextContent());        // serie
            String data = ideElement.getElementsByTagName("dhEmi").item(0).getTextContent();    
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");   
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = sdf.parse(data);
            nota.add(f.format(date));                                                           // Data/hora
            // Chave, Protocolo, Data/Hora Autorização
            NodeList protList = xml.getElementsByTagName("infProt");
            Node protNode = protList.item(0);
            Element protElement = (Element) protNode;
            nota.add(protElement.getElementsByTagName("chNFe").item(0).getTextContent());       // Chave
            nota.add(protElement.getElementsByTagName("nProt").item(0).getTextContent());       // Protocolo
            String dhRecbto = protElement.getElementsByTagName("dhRecbto").item(0).getTextContent();
            Date data_recibo = sdf.parse(dhRecbto);
            nota.add(f.format(data_recibo));                                                    // Data/Hora recibo
            // Pagamento
            NodeList pagList = xml.getElementsByTagName("pag");
            Node pagNode = pagList.item(0);
            Element pagElement = (Element) pagNode;
            // Grupo obrigatório para a NFC-e, a critério da UF. Não informar para a NF-e.
            String tPag = "";
            try {
                tPag = pagElement.getElementsByTagName("tPag").item(0).getTextContent();
            switch (tPag){
                case "01":
                    tPag = "Dinheiro";
                    break;
                case "02":
                    tPag = "Cheque";
                    break;
                case "03":
                    tPag = "Cartão de Crédito";
                    break;
                case "04":
                    tPag = "Cartão de Débito";
                    break;
                case "05":
                    tPag = "Crédito Loja";
                    break;
                case "10":
                    tPag = "Vale Alimentação";
                    break;
                case "11":
                    tPag = "Vale Refeição";
                    break;
                case "12":
                    tPag = "Vale Presente";
                    break;
                case "13":
                    tPag = "Vale Combustível";
                    break;
                case "99":
                    tPag = "Outros";
                    break;
                default:
                    tPag = "Outros";

            }
            } catch (Exception e) {
                System.out.println("Grupo obrigatório para a NFC-e, a critério da UF. Não informar para a NF-e: " + e);
            }
            nota.add(tPag);
            nota.add(itens.size()+"");

            // Tributos aprox
            try {
                nota.add(totElement.getElementsByTagName("vTotTrib").item(0).getTextContent());
            } catch (Exception e) {
                System.out.println("Valor total de tributos: " + e);
            }
            // Informação adicional
            try {
                NodeList infoList = xml.getElementsByTagName("infAdic");
                Node infoNode = infoList.item(0);
                Element infoElement = (Element) infoNode;
                nota.add(infoElement.getElementsByTagName("infAdFisco").item(0).getTextContent());
            } catch (Exception e) {
                nota.add("");
                System.out.println("Informação adicional: " + e);
            }


            //for(int z = 0; z < nota.size(); z++){
            //    System.out.println(nota.get(z)+" "+z);
            //}
            // Dante NF-e
            // java -jar nota_autorizada.xml p=nfe
            if(p.equalsIgnoreCase("nfe")){
                gerarDanfeNFe(url, emit, dest, nota, jr);
            }
            // Danfe NFC-e
            else{
               gerarDanfe(url, emit, dest, nota, jr, qrcode); 
            }
        }
        // Leitura de arquivo xml para nfse
        else{
            File inputFile = new File(xml_nfe);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document xml = builder.parse(inputFile);
            xml.getDocumentElement().normalize();
            
            // Dados do prestador
            NodeList emitList = xml.getElementsByTagName("PrestadorServico");
            Node emitNode = emitList.item(0);
            Element emitElement = (Element) emitNode;
            List <String> emit = new ArrayList<>();
            emit.add(emitElement.getElementsByTagName("Cnpj").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("InscricaoMunicipal").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("RazaoSocial").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("Endereco").item(1).getTextContent());
            emit.add(emitElement.getElementsByTagName("Numero").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("Bairro").item(0).getTextContent());
            if(emitElement.getElementsByTagName("CodigoMunicipio").item(0).getTextContent().equals("3525904")){
                emit.add("Jundiaí");
            }
            else{
                emit.add(emitElement.getElementsByTagName("CodigoMunicipio").item(0).getTextContent());
            }
            emit.add(emitElement.getElementsByTagName("Uf").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("Cep").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("Telefone").item(0).getTextContent());
            emit.add(emitElement.getElementsByTagName("Email").item(0).getTextContent());
            // Nome Fantasia Prestador
            emit.add(prestador_fantasia);
            // Debug
//            System.out.println("Prestador");
//            for(int i = 0; i < emit.size(); i++){
//                System.out.println(emit.get(i)+" "+i);
//            }
            
            // Dados do tomador
            NodeList destList = xml.getElementsByTagName("TomadorServico");
            Node destNode = destList.item(0);
            Element destElement = (Element) destNode;
            List <String> dest = new ArrayList<>();
            dest.add(destElement.getElementsByTagName("Cnpj").item(0).getTextContent());
            dest.add(destElement.getElementsByTagName("InscricaoMunicipal").item(0).getTextContent());
            dest.add(destElement.getElementsByTagName("RazaoSocial").item(0).getTextContent());
            dest.add(destElement.getElementsByTagName("Endereco").item(1).getTextContent());
            dest.add(destElement.getElementsByTagName("Numero").item(0).getTextContent());
            dest.add(destElement.getElementsByTagName("Bairro").item(0).getTextContent());
            if(destElement.getElementsByTagName("CodigoMunicipio").item(0).getTextContent().equals("3525904")){
                dest.add("Jundiaí");
            }
            else{
                dest.add(destElement.getElementsByTagName("CodigoMunicipio").item(0).getTextContent());
            }
            dest.add(destElement.getElementsByTagName("Uf").item(0).getTextContent());
            dest.add(destElement.getElementsByTagName("Cep").item(0).getTextContent());
            dest.add(destElement.getElementsByTagName("Telefone").item(0).getTextContent());
            dest.add(destElement.getElementsByTagName("Email").item(0).getTextContent());
            // Nome fantasia tomador
            dest.add(tomador_fantasia);
            // Debug
//            System.out.println("Tomador");
//            for(int i = 0; i < dest.size(); i++){
//                System.out.println(dest.get(i)+" "+i);
//            }
            
            // Dados do serviço
            NodeList serList = xml.getElementsByTagName("Servico");
            Node serNode = serList.item(0);
            Element serElement = (Element) serNode;
            List <String> ser = new ArrayList<>();
            ser.add(serElement.getElementsByTagName("ValorServicos").item(0).getTextContent());
            ser.add(serElement.getElementsByTagName("IssRetido").item(0).getTextContent());
            ser.add(serElement.getElementsByTagName("ValorIss").item(0).getTextContent());
            ser.add(serElement.getElementsByTagName("ValorIssRetido").item(0).getTextContent());
            ser.add(serElement.getElementsByTagName("BaseCalculo").item(0).getTextContent());
            ser.add(serElement.getElementsByTagName("Aliquota").item(0).getTextContent());
            ser.add(serElement.getElementsByTagName("ValorLiquidoNfse").item(0).getTextContent());
            ser.add(serElement.getElementsByTagName("ItemListaServico").item(0).getTextContent());
            ser.add(serElement.getElementsByTagName("CodigoTributacaoMunicipio").item(0).getTextContent());
            ser.add(serElement.getElementsByTagName("Discriminacao").item(0).getTextContent());
            ser.add(serElement.getElementsByTagName("CodigoMunicipio").item(0).getTextContent());
            // Debug
            //System.out.println("Serviço");
            //for(int i = 0; i < ser.size(); i++){
            //    System.out.println(ser.get(i)+" "+i);
            //}
            
            // Dados da nota
            NodeList notaList = xml.getElementsByTagName("InfNfse");
            Node notaNode = notaList.item(0);
            Element notaElement = (Element) notaNode;
            List <String> nota = new ArrayList<>();
            nota.add(notaElement.getElementsByTagName("Numero").item(0).getTextContent());
            nota.add(notaElement.getElementsByTagName("CodigoVerificacao").item(0).getTextContent());
            // Ajusta data/hora emissão
            String data_xml = notaElement.getElementsByTagName("DataEmissao").item(0).getTextContent();
            SimpleDateFormat data_entrada = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");   
            SimpleDateFormat data_saida = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = data_entrada.parse(data_xml);
            nota.add(data_saida.format(date));  
            nota.add(notaElement.getElementsByTagName("NaturezaOperacao").item(0).getTextContent());
            nota.add(notaElement.getElementsByTagName("RegimeEspecialTributacao").item(0).getTextContent());
            nota.add(notaElement.getElementsByTagName("OptanteSimplesNacional").item(0).getTextContent());
            nota.add(notaElement.getElementsByTagName("IncentivadorCultural").item(0).getTextContent());
            // Ajusta data Competência
            String data_c = notaElement.getElementsByTagName("Competencia").item(0).getTextContent();
            SimpleDateFormat data_c_entrada = new SimpleDateFormat("yyyy-MM-dd");   
            SimpleDateFormat data_c_saida = new SimpleDateFormat("dd/MM/yyyy");
            Date data_competencia = data_c_entrada.parse(data_c);
            nota.add(data_c_saida.format(data_competencia));
            // Debug
//            System.out.println("Nota");
//            for(int i = 0; i < nota.size(); i++){
//                System.out.println(nota.get(i)+" "+i);
//            }
            
            // Gera o danfe com jasper
            gerarDanfeNfse(url, emit, dest, ser, nota, xml_nfe, logo);
        }  
    }

    // Cria o Danfe com os dados do xml
    private static void gerarDanfe(String url, List<String> emit, List<String> dest, List<String> nota, JRDataSource itens, String qrcode){
        try {
            // Teste
            //String compilado = System.getProperty("user.dir") + "/danfe_nfce_80.jasper";;
            String output = "danfe.pdf"; 
            map.put("emit", emit);
            map.put("dest", dest);
            map.put("nota", nota);
            map.put("qrcode", qrcode);
            // Relatório compilado
            JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(url);
            //InputStream jasperStream = getClass().getResourceAsStream("/Foo.jasper");
            //JasperReport report = (JasperReport) JRLoader.loadObject(jasperStream);
            // Relatório nao compilado
            //JasperReport report = JasperCompileManager.compileReport(jrxml);
            JasperPrint print = JasperFillManager.fillReport(report, map, itens);
            JasperExportManager.exportReportToPdfFile(print, output);
        } catch (JRException e) {
            System.out.println("erro: "+e.getMessage());
        }
    }

    private static void gerarDanfeNFe(String url, List<String> emit, List<String> dest, List<String> nota, JRDataSource itens) {
        try {
            // Teste
            //String compilado = System.getProperty("user.dir") + "/danfe_nfce_80.jasper";;
            String output = "danfe.pdf"; 
            map.put("emit", emit);
            map.put("dest", dest);
            map.put("nota", nota);
            // Relatório compilado
            JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(url);
            JasperPrint print = JasperFillManager.fillReport(report, map, itens);
            JasperExportManager.exportReportToPdfFile(print, output);
        } catch (JRException e) {
            System.out.println("erro: "+e.getMessage());
        }
    }

    private static void gerarDanfeNfse(String url, List<String> emit, List<String> dest, List<String> ser, List<String> nota, String xml, String logo) {
        try {
            // Teste
            //String compilado = System.getProperty("user.dir") + "/danfe_nfce_80.jasper";;
            String output = "danfe.pdf"; 
            map.put("emit", emit);
            map.put("dest", dest);
            map.put("ser", ser);
            map.put("nota", nota);
            map.put("logo", logo);
            // brasao
            String brasao = Paths.get(System.getProperty("user.dir"), "danfe","brasao.png").toAbsolutePath().toString();
            map.put("brasao", brasao);
            // JrDataSource
            JRDataSource jr = new JRXmlDataSource(xml);
            // Relatório compilado
            JasperReport report = (JasperReport) JRLoader.loadObjectFromFile(url);
            JasperPrint print = JasperFillManager.fillReport(report, map, jr);
            JasperExportManager.exportReportToPdfFile(print, output);
        } catch (JRException e) {
            System.out.println("erro: "+e.getMessage());
        }
    }
     
}
