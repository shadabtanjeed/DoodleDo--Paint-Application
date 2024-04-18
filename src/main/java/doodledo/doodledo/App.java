        stage.setOnCloseRequest((WindowEvent event) -> {
            if (closeConfirmation()) {
                event.consume();
            }
        });
