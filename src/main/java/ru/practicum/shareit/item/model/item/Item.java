package ru.practicum.shareit.item.model.item;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */

@Builder
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 60)
    private String name;
    @Column(length = 500)
    private String description;
    @Column(name = "is_available")
    private Boolean isAvailable;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    private User owner;
    @Column(name = "request_id")
    private Integer requestId;
}
