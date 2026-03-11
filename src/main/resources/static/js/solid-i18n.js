/**
 * SolidSign Internationalization (i18n)
 * Centraliza dicionários e lógica de tradução.
 */

const translations = {
    pt: {
        // --- COMUNS (Cabeçalho, Botões, Credenciais) ---
        headerTitle: "Plataforma de Assinatura Digital",
        langPt: "Português", langEn: "Inglês", langEs: "Espanhol",
        btnSign: "ASSINAR DOCUMENTOS",
        btnSignXml: "ASSINAR XMLs",
        btnAddFile: "+ Adicionar Arquivo",
        btnAddXml: "+ Adicionar XML",
        colType: "Tipo",
        colName: "Nome do Arquivo",
        colAction: "Ação",
        emptyState: "Nenhum arquivo selecionado.",
        lblCert: "Arquivo do Certificado (PFX/P12)*:",
        lblPass: "Senha do Certificado*:",
        lblStd: "Padrão de Assinatura:",
        lblHash: "Algoritmo Hash:",
        step4Title: "4. Credenciais 🔐", // No XML é passo 3, ajustaremos via ID específico se necessário ou manteremos genérico
        stepCredsTitle: "Credenciais 🔐", // Título genérico para reuso
        stepCredsSub: "Selecione o certificado digital.",
        resSuccess: "Processo Concluído! Links para Download:",
        proc: "PROCESSANDO...",
        err: "Erro: ",
        selDoc: "Por favor, selecione ao menos um documento.",
        
        // --- ESPECÍFICOS PDF (PAdES) ---
        headerSub_pdf: "Padrão PAdES (PDF Advanced Electronic Signature)",
        step1Title_pdf: "1. Seleção de Documentos 📄",
        step1Sub_pdf: "Adicione os arquivos PDF que serão assinados.",
        colPages: "Aplicação Visual (Páginas)",
        pageHelper: "* Indique apenas as páginas onde a imagem/texto deve aparecer (Ex: \"1\", \"Última\", \"1-3\"). A assinatura digital protege o documento todo.",
        phPages: "Ex: 1, 1-3",
        step2Title_pdf: "2. Design da Assinatura (Opcional) 🖌️",
        step2Sub_pdf: "Configure a aparência da estampa visual.",
        tabImg: "IMAGEM / RUBRICA",
        tabTxt: "TEXTO ADICIONAL",
        lblUpload: "Upload da Imagem:",
        lblFormats: "* Formatos: PNG, JPG.",
        lblX: "Posição X:", lblY: "Posição Y:", lblW: "Largura:", lblH: "Altura:",
        chkEnableText: "Habilitar Texto",
        lblTextContent: "Conteúdo:",
        phName: "Nome / CPF",
        lblFontSize: "Tamanho:",
        step3Title_pdf: "3. Dados Complementares (Opcional) 🗃️",
        step3Sub_pdf: "Adicione metadados personalizados.",
        btnAddMeta: "+ Metadado",
        phKey: "Chave (Ex: Local)", phValue: "Valor (Ex: SP)",
		lblImgArea: "Área da Imagem",

        // --- ESPECÍFICOS XML (XAdES) ---
        headerSub_xml: "Padrão XAdES (XML Advanced Electronic Signature)",
        step1Title_xml: "1. Seleção de Arquivos XML 📄",
        step1Sub_xml: "Adicione os arquivos XML para assinatura em lote.",
        step2Title_xml: "2. Parâmetros de Assinatura ⚙️",
        step2Sub_xml: "Configure o padrão XAdES e XPath.",
        lblLevel: "Nível do Perfil:",
        lblNodes: "IDs de Nós (XPath):",
        nodeHelper: "* Vazio = Assina tudo (Enveloped).",
        btnAddNode: "+ Node ID",
        emptyNode: "Assinando documento completo (padrão).",
        phNode: "Ex: NFe1234..."
    },
    en: {
        // --- COMMONS ---
        headerTitle: "Digital Signature Platform",
        langPt: "Portuguese", langEn: "English", langEs: "Spanish",
        btnSign: "SIGN DOCUMENTS",
        btnSignXml: "SIGN XMLs",
        btnAddFile: "+ Add File",
        btnAddXml: "+ Add XML",
        colType: "Type",
        colName: "File Name",
        colAction: "Action",
        emptyState: "No file selected.",
        lblCert: "Certificate File (PFX/P12)*:",
        lblPass: "Certificate Password*:",
        lblStd: "Signature Standard:",
        lblHash: "Hash Algorithm:",
        stepCredsTitle: "Credentials 🔐",
        stepCredsSub: "Select the digital certificate.",
        resSuccess: "Success! Download Links:",
        proc: "PROCESSING...",
        err: "Error: ",
        selDoc: "Please select at least one document.",

        // --- PDF ---
        headerSub_pdf: "PAdES Standard (PDF Advanced Electronic Signature)",
        step1Title_pdf: "1. Document Selection 📄",
        step1Sub_pdf: "Add PDF files to be signed.",
        colPages: "Visual App (Pages)",
        pageHelper: "* Indicate pages for visual stamp only. Digital signature applies to whole doc.",
        phPages: "Ex: 1, 1-3",
        step2Title_pdf: "2. Signature Design (Optional) 🖌️",
        step2Sub_pdf: "Configure visual appearance.",
        tabImg: "IMAGE / RUBRIC",
        tabTxt: "ADDITIONAL TEXT",
        lblUpload: "Upload Image:",
        lblFormats: "* Formats: PNG, JPG.",
        lblX: "Pos X:", lblY: "Pos Y:", lblW: "Width:", lblH: "Height:",
        chkEnableText: "Enable Text",
        lblTextContent: "Content:",
        phName: "Name / ID",
        lblFontSize: "Size:",
        step3Title_pdf: "3. Complementary Data (Optional) 🗃️",
        step3Sub_pdf: "Add custom metadata.",
        btnAddMeta: "+ Metadata",
        phKey: "Key (Ex: Loc)", phValue: "Value (Ex: NY)",
		lblImgArea: "Image Area",

        // --- XML ---
        headerSub_xml: "XAdES Standard (XML Advanced Electronic Signature)",
        step1Title_xml: "1. XML File Selection 📄",
        step1Sub_xml: "Add XML files for batch signing.",
        step2Title_xml: "2. Signature Parameters ⚙️",
        step2Sub_xml: "Configure XAdES and XPath.",
        lblLevel: "Profile Level:",
        lblNodes: "Node IDs (XPath):",
        nodeHelper: "* Empty = Sign all (Enveloped).",
        btnAddNode: "+ Node ID",
        emptyNode: "Signing full document (default).",
        phNode: "Ex: NFe1234..."
    },
    es: {
        // --- COMUNES ---
        headerTitle: "Plataforma de Firma Digital",
        langPt: "Portugués", langEn: "Inglés", langEs: "Español",
        btnSign: "FIRMAR DOCUMENTOS",
        btnSignXml: "FIRMAR XMLs",
        btnAddFile: "+ Agregar Archivo",
        btnAddXml: "+ Agregar XML",
        colType: "Tipo",
        colName: "Nombre del Archivo",
        colAction: "Acción",
        emptyState: "Ningún archivo seleccionado.",
        lblCert: "Archivo de Certificado (PFX/P12)*:",
        lblPass: "Contraseña del Certificado*:",
        lblStd: "Estándar de Firma:",
        lblHash: "Algoritmo Hash:",
        stepCredsTitle: "Credenciales 🔐",
        stepCredsSub: "Seleccione el certificado digital.",
        resSuccess: "¡Éxito! Enlaces de Descarga:",
        proc: "PROCESANDO...",
        err: "Error: ",
        selDoc: "Por favor, seleccione al menos un documento.",

        // --- PDF ---
        headerSub_pdf: "Estándar PAdES (PDF Advanced Electronic Signature)",
        step1Title_pdf: "1. Selección de Documentos 📄",
        step1Sub_pdf: "Agregue los archivos PDF.",
        colPages: "Aplicación Visual (Páginas)",
        pageHelper: "* Indique solo las páginas para la estampa visual. La firma digital aplica a todo.",
        phPages: "Ej: 1, 1-3",
        step2Title_pdf: "2. Diseño de Firma (Opcional) 🖌️",
        step2Sub_pdf: "Configure la apariencia visual.",
        tabImg: "IMAGEN / RÚBRICA",
        tabTxt: "TEXTO ADICIONAL",
        lblUpload: "Subir Imagen:",
        lblFormats: "* Formatos: PNG, JPG.",
        lblX: "Pos X:", lblY: "Pos Y:", lblW: "Ancho:", lblH: "Alto:",
        chkEnableText: "Habilitar Texto",
        lblTextContent: "Contenido:",
        phName: "Nombre / DNI",
        lblFontSize: "Tamaño:",
        step3Title_pdf: "3. Datos Complementarios (Opcional) 🗃️",
        step3Sub_pdf: "Agregue metadados personalizados.",
        btnAddMeta: "+ Metadato",
        phKey: "Clave (Ej: Ubicación)", phValue: "Valor (Ej: Madrid)",
		lblImgArea: "Área de la Imagen",
		
        // --- XML ---
        headerSub_xml: "Estándar XAdES (XML Advanced Electronic Signature)",
        step1Title_xml: "1. Selección de XML 📄",
        step1Sub_xml: "Agregue archivos XML.",
        step2Title_xml: "2. Parámetros de Firma ⚙️",
        step2Sub_xml: "Configure XAdES y XPath.",
        lblLevel: "Nivel de Perfil:",
        lblNodes: "IDs de Nodo (XPath):",
        nodeHelper: "* Vacío = Firmar todo (Enveloped).",
        btnAddNode: "+ Node ID",
        emptyNode: "Firmando documento completo.",
        phNode: "Ej: NFe1234..."
    }
};

let currentLang = 'pt';

function setLanguage(lang) {
    currentLang = lang;
    document.documentElement.lang = lang;
    
    // 1. Atualiza textos estáticos (data-i18n)
    document.querySelectorAll('[data-i18n]').forEach(el => {
        const key = el.getAttribute('data-i18n');
        if (translations[lang][key]) {
            el.textContent = translations[lang][key];
        }
    });

    // 2. Atualiza Placeholders comuns
    updatePlaceholders('.row-pages', 'phPages');
    updatePlaceholders('.meta-key', 'phKey');
    updatePlaceholders('.meta-val', 'phValue');
    updatePlaceholders('input[name="nodeId"]', 'phNode');
    updatePlaceholders('#tc-text', 'phName');

    // 3. Atualiza Bandeiras
    document.querySelectorAll(".flag").forEach(f => f.classList.remove("active"));
    const map = { pt: 'ptbr', en: 'en', es: 'es' };
    const activeFlag = document.querySelector(`img[src*="${map[lang]}.png"]`);
    if(activeFlag) activeFlag.classList.add("active");
}

// Helper para traduzir no JS
function t(key) {
    return translations[currentLang][key] || key;
}

// Helper para placeholders dinâmicos
function updatePlaceholders(selector, key) {
    document.querySelectorAll(selector).forEach(el => {
        if(translations[currentLang][key]) el.placeholder = translations[currentLang][key];
    });
}