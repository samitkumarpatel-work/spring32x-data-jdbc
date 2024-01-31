CREATE TABLE Department (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE Address (
     id SERIAL PRIMARY KEY,
     location VARCHAR(255) NOT NULL
);

CREATE TABLE Employee (
      id SERIAL PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      department INTEGER REFERENCES Department(id),
      address INTEGER REFERENCES Address(id)
);


