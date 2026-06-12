-- 1. Crear la base de datos si no existe
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = N'EventSecurityDB')
BEGIN
    CREATE DATABASE EventSecurityDB;
END
GO

-- 2. Seleccionar la base de datos
USE EventSecurityDB;
GO

-- 3. Crear tabla 'users'
IF OBJECT_ID(N'dbo.users', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.users (
        id INT IDENTITY(1,1) PRIMARY KEY,
        nombre VARCHAR(100) NOT NULL,
        email VARCHAR(100) UNIQUE NOT NULL,
        password VARCHAR(256) NOT NULL,
        status BIT NOT NULL
    );
END
GO

-- 4. Crear tabla 'eventos'
IF OBJECT_ID(N'dbo.eventos', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.eventos (
        id INT IDENTITY(1,1) PRIMARY KEY,
        nombre VARCHAR(150) NOT NULL,
        descripcion VARCHAR(300),
        fecha DATETIME NOT NULL,
        lugar VARCHAR(150) NOT NULL,
        cupos_totales INT NOT NULL,
        cupos_disponibles INT NOT NULL,
        status BIT NOT NULL
    );
END
GO

-- 5. Crear tabla 'clientes'
IF OBJECT_ID(N'dbo.clientes', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.clientes (
        id INT IDENTITY(1,1) PRIMARY KEY,
        dni VARCHAR(20) UNIQUE NOT NULL,
        nombre VARCHAR(150) NOT NULL,
        email VARCHAR(100),
        telefono VARCHAR(20),
        status BIT NOT NULL
    );
END
GO

-- 6. Crear tabla 'ventas'
IF OBJECT_ID(N'dbo.ventas', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ventas (
        id INT IDENTITY(1,1) PRIMARY KEY,
        fecha DATETIME NOT NULL,
        cliente_id INT NOT NULL,
        evento_id INT NOT NULL,
        cantidad INT NOT NULL,
        total DECIMAL(10,2) NOT NULL,
        status BIT NOT NULL,
        CONSTRAINT FK_ventas_clientes FOREIGN KEY (cliente_id) REFERENCES dbo.clientes(id),
        CONSTRAINT FK_ventas_eventos FOREIGN KEY (evento_id) REFERENCES dbo.eventos(id)
    );
END
GO

-- 7. Crear tabla 'pagos'
IF OBJECT_ID(N'dbo.pagos', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.pagos (
        id INT IDENTITY(1,1) PRIMARY KEY,
        venta_id INT NOT NULL,
        metodo_pago VARCHAR(30) NOT NULL,
        monto DECIMAL(10,2) NOT NULL,
        fecha DATETIME NOT NULL,
        CONSTRAINT FK_pagos_ventas FOREIGN KEY (venta_id) REFERENCES dbo.ventas(id)
    );
END
GO

-- 8. Crear tabla 'asistencias'
IF OBJECT_ID(N'dbo.asistencias', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.asistencias (
        id INT IDENTITY(1,1) PRIMARY KEY,
        venta_id INT NOT NULL,
        fecha_ingreso DATETIME NOT NULL,
        CONSTRAINT FK_asistencias_ventas FOREIGN KEY (venta_id) REFERENCES dbo.ventas(id)
    );
END
GO
