import React from 'react';
import { Layout } from 'antd';
import { Link, useLocation } from 'react-router-dom';

const { Header, Content } = Layout;

export default function MainLayout({ children }) {
    const location = useLocation();

    const navItemStyle = (path) => ({
        padding: '8px 16px',
        borderRadius: '8px',
        marginLeft: 12,
        backgroundColor: location.pathname === path ? '#40e0d0' : 'transparent',
        color: location.pathname === path ? '#fff' : '#ccc',
        textDecoration: 'none',
        fontWeight: 500,
        transition: '0.3s',
    });

    return (
        <Layout style={{ minHeight: '100vh', background: '#ffffff' }}>
            <Header
                style={{
                    background: 'linear-gradient(to right, #2C3E3F, #1A1F1F)',
                    padding: '0 24px',
                    display: 'flex',
                    alignItems: 'center',
                    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                    zIndex: 10
                }}
            >
                <div style={{ flexGrow: 1, color: 'white', fontWeight: 'bold', fontSize: 20 }}>
                    🌿 WebMagazine
                </div>

                <div style={{ display: 'flex' }}>
                    <Link to="/" style={navItemStyle('/')}>Товары</Link>
                    <Link to="/orders" style={navItemStyle('/orders')}>Заказы</Link>
                </div>
            </Header>

            <Content style={{ padding: '24px' }}>
                {children}
            </Content>
        </Layout>
    );
}


