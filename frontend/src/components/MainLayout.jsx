import React from 'react';
import { Layout, Menu } from 'antd';
import { Link, useLocation } from 'react-router-dom';

const { Header, Content } = Layout;

export default function MainLayout({ children }) {
    const location = useLocation();

    const items = [
        { key: '/', label: <Link to="/">Товары</Link> },
        { key: '/orders', label: <Link to="/orders">Заказы</Link> },
    ];

    return (
        <Layout style={{ minHeight: '100vh', background: '#141414' }}>
            <Header style={{ background: '#1f1f1f' }}>
                <Menu
                    mode="horizontal"
                    theme="dark"
                    selectedKeys={[location.pathname]}
                    items={items}
                />
            </Header>
            <Content style={{ padding: '24px' }}>
                {children}
            </Content>
        </Layout>
    );
}

