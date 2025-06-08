const WebSocket = require('ws');
const mysql = require('mysql2');

// Servidor WebSocket
const wss = new WebSocket.Server({ port: 3001 }, () => {
  console.log('✅ Servidor WebSocket iniciado en ws://localhost:3001');
});

// Conexión MySQL
const connection = mysql.createConnection({
  host: '172.17.0.2',
  user: 'admin',
  password: 'WXzU4jdi9wWy',
  database: 'sistema_tienda'
});

connection.connect((err) => {
  if (err) {
    console.error('❌ Error al conectar con la base de datos:', err.message);
  } else {
    console.log('✅ Conectado a la base de datos MySQL con éxito');
  }
});

// Objeto para almacenar los últimos estados de las tablas
let lastTableStates = {
  ventas: { count: 0, maxId: 0 },
  detalle_venta: { count: 0, maxId: 0 }
};

// Función para consultar cambios en las tablas
async function checkTableChanges() {
  try {
    const [ventasResult, detalleResult] = await Promise.all([
      queryTableState('ventas'),
      queryTableState('detalle_venta')
    ]);

    const cambios = {
      ventas: checkForChanges('ventas', ventasResult),
      detalle_venta: checkForChanges('detalle_venta', detalleResult)
    };

    if (cambios.ventas || cambios.detalle_venta) {
      notifyClients();
    }
  } catch (error) {
    console.error('❌ Error al verificar cambios:', error.message);
  }
}

// Función auxiliar para consultar el estado de una tabla
function queryTableState(tableName) {
  return new Promise((resolve, reject) => {
    connection.query(
      `SELECT COUNT(*) as count, MAX(id) as maxId FROM ${tableName}`,
      (err, results) => {
        if (err) return reject(err);
        resolve(results[0]);
      }
    );
  });
}

// Función auxiliar para detectar cambios
function checkForChanges(tableName, currentState) {
  const lastState = lastTableStates[tableName];
  const hasChanged = (
    currentState.count !== lastState.count ||
    currentState.maxId !== lastState.maxId
  );

  if (hasChanged) {
    console.log(`🔄 Cambios detectados en ${tableName}:`, {
      antes: lastState,
      ahora: currentState
    });
    lastTableStates[tableName] = currentState;
    return true;
  }
  return false;
}

// Función para notificar a los clientes
function notifyClients() {
  const message = JSON.stringify({ tipo: "ventas_actualizadas" });
  
  wss.clients.forEach(client => {
    if (client.readyState === WebSocket.OPEN) {
      client.send(message);
    }
  });
  
  console.log("📣 Notificación de cambios enviada a los clientes");
}

// Cargar estado inicial de las tablas
async function initializeTableStates() {
  try {
    const [ventasResult, detalleResult] = await Promise.all([
      queryTableState('ventas'),
      queryTableState('detalle_venta')
    ]);

    lastTableStates.ventas = ventasResult;
    lastTableStates.detalle_venta = detalleResult;

    console.log('📊 Estado inicial de las tablas cargado:', lastTableStates);
  } catch (error) {
    console.error('❌ Error al cargar estado inicial:', error.message);
  }
}

// Inicializar e iniciar el intervalo
initializeTableStates().then(() => {
  setInterval(checkTableChanges, 2000); // Verificar cada 2 segundos
});

// Manejo de conexiones WebSocket
wss.on('connection', (ws) => {
  console.log('🔗 Cliente WebSocket conectado');

  ws.on('message', (message) => {
    console.log('📩 Mensaje recibido del cliente:', message);

    if (message === 'get_pedidos') {
      // Enviar todos los pedidos y detalles combinados
      connection.query('SELECT * FROM ventas', (err, ventas) => {
        if (err) {
          ws.send(JSON.stringify({ error: 'Error al consultar ventas' }));
          return;
        }

        connection.query('SELECT * FROM detalle_venta', (err2, detalles) => {
          if (err2) {
            ws.send(JSON.stringify({ error: 'Error al consultar detalle_venta' }));
            return;
          }

          ws.send(JSON.stringify({ ventas, detalle_venta: detalles }));
          console.log(`📦 Enviados todos los pedidos y detalles al cliente`);
        });
      });
    } else {
      console.warn('⚠️ Mensaje desconocido:', message);
    }
  });

  ws.on('close', () => {
    console.log('🔌 Cliente WebSocket desconectado');
  });

  ws.on('error', (err) => {
    console.error('❌ Error en conexión WebSocket:', err.message);
  });
});