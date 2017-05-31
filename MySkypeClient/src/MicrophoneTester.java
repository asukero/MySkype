class MicrophoneTester extends Microphone {
    private GUI frame;

    public MicrophoneTester(GUI frame) {
        this.frame = frame;
    }

    @Override
    protected byte[] readFromMicrophone() {
        byte[] buffer = new byte[SoundPacket.defaultDataLength];
        this.microphone.read(buffer, 0, buffer.length);

        return buffer;
    }

    @Override
    public void run() {
        try {
            this.openMicrophoneLine();
        } catch (Exception e) {
            Utils.displayError(
                "Microphone not detected.\n"
                + "Press OK to close this program",
                this.frame.getRootPane());

            System.exit(0);
        }

        while (true) {
            Utils.sleep(10);

            if (this.microphone.available() <= 0) continue;

            long bufferCount = this.getBufferCount(this.readFromMicrophone());
            this.frame.getMicrophoneLevel().setValue((int) bufferCount);
        }
    }

    public void close() {
        if (this.microphone != null) this.microphone.close();

        MicrophoneTester.currentThread().interrupt();
    }
}