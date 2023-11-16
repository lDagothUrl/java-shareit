package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.model.item.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 2000)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id")
    private User requestor;
    private LocalDateTime created;
    @Transient
    private List<Item> items;
}
