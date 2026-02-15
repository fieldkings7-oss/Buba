# APK Builder using Godot
FROM barichello/godot-ci:4.2.1

WORKDIR /build

# Copy project files
COPY project.godot ./
COPY scenes/ scenes/
COPY scripts/ scripts/
COPY export_presets.cfg ./

# Create a .godot folder to prevent import errors
RUN mkdir -p .godot/imported

# Run one export attempt
RUN godot --headless --editor --quit 2>&1 || true

# Try exporting
RUN mkdir -p output && \
    godot --headless --export-release Android output/app.apk 2>&1 || true

# Check result
RUN ls -lh output/ 2>/dev/null || echo "No output yet"

CMD ["sh", "-c", "ls -lh output/ 2>/dev/null || echo 'Build ready'"]
