CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    title TEXT NOT NULL DEFAULT 'Untitled',
    composer TEXT,
    key_signature TEXT NOT NULL DEFAULT 'C',
    mode TEXT NOT NULL DEFAULT 'major',
    time_signature TEXT NOT NULL DEFAULT '4/4',
    bpm INTEGER NOT NULL DEFAULT 120,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE instruments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name TEXT NOT NULL,
    type TEXT NOT NULL CHECK (type IN ('grand', 'single')),
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE notes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    instrument_id UUID NOT NULL REFERENCES instruments(id) ON DELETE CASCADE,
    type TEXT NOT NULL CHECK (type IN ('note', 'rest')),
    letter TEXT,
    octave INTEGER,
    duration NUMERIC NOT NULL,
    staff TEXT CHECK (staff IN ('treble', 'bass')),
    chord_id TEXT,
    manual_spelling BOOLEAN NOT NULL DEFAULT false,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_projects_user_id ON projects(user_id);
CREATE INDEX idx_instruments_project_id ON instruments(project_id);
CREATE INDEX idx_notes_instrument_id ON notes(instrument_id);

ALTER TABLE projects ENABLE ROW LEVEL SECURITY;
ALTER TABLE instruments ENABLE ROW LEVEL SECURITY;
ALTER TABLE notes ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can CRUD own projects"
    ON projects FOR ALL
    USING (auth.uid() = user_id);

CREATE POLICY "Users can CRUD own instruments"
    ON instruments FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM projects
            WHERE projects.id = instruments.project_id
            AND projects.user_id = auth.uid()
        )
    );

CREATE POLICY "Users can CRUD own notes"
    ON notes FOR ALL
    USING (
        EXISTS (
            SELECT 1 FROM instruments
            JOIN projects ON projects.id = instruments.project_id
            WHERE instruments.id = notes.instrument_id
            AND projects.user_id = auth.uid()
        )
    );
