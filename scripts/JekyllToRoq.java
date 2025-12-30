//JAVA 25

final static String JEKYLL_BLOG_PATH = "./_posts/";
final static String ROQ_BLOG_PATH = "../content/posts/tmp/";


private String transformJekyllToRoq(String content, String targetFolder) {
    // Regex pour capturer le front matter
    Pattern frontMatterPattern = Pattern.compile("^---(.*?)---", Pattern.DOTALL);
    Matcher matcher = frontMatterPattern.matcher(content);

    if (matcher.find()) {
        String jekyllFrontMatter = matcher.group(1);
        String body = content.substring(matcher.end()).trim();

        // Suppression des balises meta SEO de Jekyll (souvent problématiques avec Roq)
        body = body.replaceAll("<meta content=\"\\{\\{.*?property=\"og:image\">", "");

        String headOfBody = body.substring(0, Math.min(body.length(), 300));

        Pattern imageOnlyPattern = Pattern.compile("^!\\[.*?\\]\\(.*?/assets/images/([^\\s\\)]+).*?\\)(?:\\{:.*?\\})*", Pattern.MULTILINE);
        Matcher imageMatcher = imageOnlyPattern.matcher(headOfBody);

        String imageForFrontMatter = "";
        String figCaption = "";


        if (imageMatcher.find()) {
            // On a trouvé une image, on l'extrait
            String fullImagePath = imageMatcher.group(1);
            imageForFrontMatter = Paths.get(fullImagePath).getFileName().toString();
            String imageFullMatch = imageMatcher.group(0);

            // On supprime l'image du corps
            body = body.replace(imageFullMatch, "").trim();

            // 2. Détection d'un lien de crédit (légende) immédiatement après l'image
            // On cherche un pattern [Texte](URL) suivi de n'importe quel nombre d'attributs {:...}
            // Le (?:\\{:.*?\\})* permet de capturer zéro, un ou plusieurs blocs d'attributs
            Pattern linkPattern = Pattern.compile("^\\[(.*?)\\]\\(.*?\\)(?:\\{:.*?\\})*", Pattern.DOTALL);
            Matcher linkMatcher = linkPattern.matcher(body);

            if (linkMatcher.find()) {
                // Le texte du lien devient notre figCaption
                figCaption = linkMatcher.group(1);
                IO.println("figCaption: " + figCaption);
                // On supprime ce lien ET tous ses attributs du corps du texte
                body = body.replace(linkMatcher.group(0), "").trim();

                // Optionnel : supprimer d'éventuels <br/> qui traîneraient juste après
                if (body.startsWith("<br/>")) {
                    body = body.substring(5).trim();
                }
            }
        }


        // Nettoyage du corps du texte (suppression des attributs Kramdown)
        body = body.replace("{:target=\"_blank\"}", "");

        // Nettoyage de TOUTES les images restantes dans le corps
        // Remplace ![alt]({{ site... }}/path/file.jpg){: .align-center} par ![alt](file.jpg)
        body = body.replaceAll("!\\[(.*?)\\]\\(.*?/assets/images/.*?/([^\\s\\)]+).*?\\)(?:\\{:.*?\\})*", "![$1]($2)");
        // Suppression de TOUS les attributs Jekyll résiduels {: ... } dans tout le document
        // Cela couvre {:style="..."}, {: .align-right}, {:target="..."} etc.
        body = body.replaceAll("\\{:.*?\\}", "");
        // Bracket protection
        body = protectRoqExpressions(body);

        // Extraction des données Jekyll
        String title = extractValue(jekyllFrontMatter, "title");
        String description = extractValue(jekyllFrontMatter, "excerpt");

        // Extraction des tags (on combine categories et tags de Jekyll)
        String tagsBlock = extractRawYamlList(jekyllFrontMatter, "tags");

        // Construction du nouveau Front Matter
        StringBuilder roqFront = new StringBuilder("---\n");
        roqFront.append("title: \"").append(title).append("\"\n");
        roqFront.append("description: \"").append(description.replace("\"", "\\\"")).append("\"\n");
        roqFront.append("link: /").append(targetFolder).append("\n");
        roqFront.append("tags: \n").append(tagsBlock).append("\n");
        if (!imageForFrontMatter.isEmpty()) {
            roqFront.append("image: ").append(imageForFrontMatter.isEmpty() ? "image-illustration.jpg" : imageForFrontMatter).append("\n");
            roqFront.append("figCaption: \"").append(figCaption.isEmpty() ? "@wildagsx" : figCaption).append("\"\n");
        }
        roqFront.append("author: wilda\n");
        roqFront.append("---\n\n");

        return roqFront.toString() + body;
    }
    return content;
}

private String extractValue(String yaml, String key) {
    // On capture la valeur après la clé, en ignorant les guillemets entourant s'ils existent
    Pattern p = Pattern.compile(key + ":\\s*(.*)(?:\\n|$)");
    Matcher m = p.matcher(yaml);
    if (m.find()) {
        String value = m.group(1).trim();
        // On retire les guillemets de début et de fin s'ils sont présents dans Jekyll
        return value.replaceAll("^\"|\"$|^'|'$", "").trim();
    }
    return "";
}
private String extractRawYamlList(String yaml, String key) {
    // Capture tout le bloc qui commence par "-" après la clé spécifiée
    Pattern p = Pattern.compile(key + ":\\s*\\n((?:\\s*-.*?(?:\\n|$))+)");
    Matcher m = p.matcher(yaml);
    if (m.find()) {
        return m.group(1).stripTrailing();
    }
    return "  - blog"; // Valeur par défaut si aucun tag n'est trouvé
}

private String extractFirstImage(String body) {
    // Cherche ![alt text](url) et extrait le nom du fichier
    Pattern p = Pattern.compile("!\\[.*?\\]\\(.*?/([^/\\)]+\\.(?:jpg|png|jpeg|webp))\\)");
    Matcher m = p.matcher(body);
    return m.find() ? m.group(1) : "";
}

private String extractFigCaption(String body) {
    // Cherche les liens de crédit type [© Pauline]
    Pattern p = Pattern.compile("\\[(©.*?)\\]");
    Matcher m = p.matcher(body);
    return m.find() ? m.group(1) : "";
}

private String extractImageSourceSubPath(String body) {
    // Capture la partie après /assets/images/ (ex: javelit/gondola.jpg)
    Pattern p = Pattern.compile("/assets/images/([^\\s\\)]+\\.(?:jpg|png|jpeg|webp|svg))");
    Matcher m = p.matcher(body);
    return m.find() ? m.group(1) : "";
}

private String protectRoqExpressions(String body) {
    // On ne protège que si l'accolade est suivie immédiatement par une lettre
    // sans espace, ce qui correspond aux expressions Qute/Roq.
    // On ajoute un espace : {events -> { events
    return body.replaceAll("\\{([a-zA-Z])", "{ $1");
}

void main() {

    Path jekyllPath = Paths.get(JEKYLL_BLOG_PATH);
    Path roqPath = Paths.get(ROQ_BLOG_PATH);
    Path jekyllImagesBase = Paths.get(jekyllPath + "/images/");
    IO.println(jekyllImagesBase);

    // First step: files migration
    try (var files = Files.list(jekyllPath)) {
        files.filter(path -> path.toString().endsWith(".md"))
                .forEach(jekyllFile -> {
                    try {
                        // 1. Déterminer le nom du dossier (ex: 2025-01-12-titre)
                        String fileName = jekyllFile.getFileName().toString();
                        String folderName = fileName.replace(".md", "");

                        // 2. Créer le dossier de destination
                        Path targetFolder = roqPath.resolve(folderName);
                        Files.createDirectories(targetFolder);

                        // 3. Définir le fichier cible index.md
                        Path targetFile = targetFolder.resolve("index.md");

                        // 4. Copier le contenu
                        String content = Files.readString(jekyllFile);
                        String imageSourceSubPath = extractImageSourceSubPath(content);

                        if (!imageSourceSubPath.isEmpty()) {
                            Path sourceImagePath = jekyllImagesBase.resolve(imageSourceSubPath);
                            if (Files.exists(sourceImagePath)) {
                                // Copie de l'image à la racine du dossier du post
                                Files.copy(sourceImagePath, targetFolder.resolve(sourceImagePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                            }
                        }

                        String transformedContent = transformJekyllToRoq(content, targetFolder.getName((targetFolder.getNameCount() != 0 ? targetFolder.getNameCount() - 1 : 0)).toString());
                        Files.writeString(targetFile, transformedContent);
                        IO.println("Migré : " + fileName + " -> " + folderName + "/index.md");
                    } catch (IOException e) {
                        System.err.println("Erreur lors de la migration de " + jekyllFile + " : " + e.getMessage());
                    }
                });
    } catch (IOException e) {
        e.printStackTrace();
    }

}
